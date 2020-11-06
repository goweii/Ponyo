package per.goweii.ponyo.startup.gradle.plugin

import jdk.internal.org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.ClassReader

import java.util.jar.JarEntry
import java.util.jar.JarFile

class ScanUtils {

    static boolean shouldProcessPreDexJar(String path) {
        return !path.contains("com.android.support") && !path.contains("/android/m2repository")
    }

    static boolean shouldProcessClass(String entryName) {
        return entryName != null
    }

    static void scanJar(File file) {
        if (file) {
            def jarFile = new JarFile(file)
            Enumeration<JarEntry> enumeration = jarFile.entries()
            while (enumeration.hasMoreElements()) {
                JarEntry jarEntry = enumeration.nextElement()
                String entryName = jarEntry.getName()
                if (!entryName.endsWith(".class")) continue
                InputStream inputStream = jarFile.getInputStream(jarEntry)
                scanClass(inputStream, entryName)
                inputStream.close()
            }
            jarFile.close()
        }
    }

    static void scanClass(File file, String entryName) {
        scanClass(new FileInputStream(file), entryName)
    }

    static void scanClass(InputStream inputStream, String entryName) {
        ClassReader cr = new ClassReader(inputStream)
        ClassNode cn = new ClassNode()
        cr.accept(cn, ClassReader.SKIP_CODE)
        boolean hasAnnotation = false
        boolean hasInterface = false
        def ans = cn.invisibleAnnotations
        ans.each { an ->
            if (ScanConst.ANNOTATION_DESC == an.desc) {
                hasAnnotation = true
            }
        }
        def ins = cn.interfaces
        ins.each { inf ->
            if (ScanConst.INTERFACE_CLASS_NAME == inf) {
                hasInterface = true
            }
        }
        if (hasAnnotation && hasInterface) {
            String className = entryName.substring(0, entryName.length() - 6)
            className.replaceAll("/", ".")
            StartupTransform.registerList.add(className)
        }
    }
}