package per.goweii.ponyo.startup.gradle.plugin

import jdk.internal.org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.ClassReader

import java.util.jar.JarEntry
import java.util.jar.JarFile

class ScanUtils {

    static void scanJar(File jarFile) {
        if (jarFile) {
            def file = new JarFile(jarFile)
            Enumeration enumeration = file.entries()
            while (enumeration.hasMoreElements()) {
                JarEntry jarEntry = (JarEntry) enumeration.nextElement()
                String entryName = jarEntry.getName()
                if (!entryName.endsWith(".class")) continue
                InputStream inputStream = file.getInputStream(jarEntry)
                scanClass(inputStream, entryName)
                inputStream.close()
            }
            file.close()
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