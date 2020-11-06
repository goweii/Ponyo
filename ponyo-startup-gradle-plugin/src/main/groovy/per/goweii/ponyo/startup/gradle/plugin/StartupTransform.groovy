package per.goweii.ponyo.startup.gradle.plugin

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import org.gradle.api.Project
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.FileUtils

class StartupTransform extends Transform {
    final Project project

    List<ScanMeta> scanMetas = new ArrayList<>()
    File fileCenterClass = null

    StartupTransform(Project project) {
        this.project = project
    }

    @Override
    String getName() {
        return ScanConst.TRANSFORM_NAME
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS
    }

    @Override
    Set<QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    @Override
    boolean isIncremental() {
        return false
    }

    @Override
    void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        super.transform(transformInvocation)
        long startTime = System.currentTimeMillis()
        transformInvocation.inputs.each { TransformInput input ->
            transformDir(transformInvocation, input)
            //transformJar(transformInvocation, input)
        }
        generateClass()
        long endTime = System.currentTimeMillis()
        Logger.i("transform cost:" + (endTime - startTime) + "ms")
    }

    private void transformDir(
            TransformInvocation transformInvocation,
            TransformInput input
    ) {
        boolean leftSlash = File.separator == '/'
        input.directoryInputs.each { directoryInput ->
            File dest = transformInvocation.outputProvider.getContentLocation(
                    directoryInput.name,
                    directoryInput.contentTypes,
                    directoryInput.scopes,
                    Format.DIRECTORY
            )
            String root = directoryInput.file.absolutePath
            if (!root.endsWith(File.separator))
                root += File.separator
            directoryInput.file.eachFileRecurse { File file ->
                def path = file.absolutePath.replace(root, '')
                if (!leftSlash) {
                    path = path.replaceAll("\\\\", "/")
                }
                if(file.isFile() && ScanUtils.shouldProcessClass(path)){
                    ScanUtils.scanClass(file, path)
                }
            }
            FileUtils.copyDirectory(directoryInput.file, dest)
        }
    }

    private void transformJar(
            TransformInvocation transformInvocation,
            TransformInput input
    ) {
        input.jarInputs.each { jarInput ->
            String destName = jarInput.name
            def hexName = DigestUtils.md5Hex(jarInput.file.absolutePath)
            if (destName.endsWith(".jar")) {
                destName = destName.substring(0, destName.length() - 4)
            }
            File src = jarInput.file
            File dest = transformInvocation.outputProvider.getContentLocation(
                    destName + "_" + hexName,
                    jarInput.contentTypes,
                    jarInput.scopes,
                    Format.JAR
            )
            if (ScanUtils.shouldProcessPreDexJar(src.absolutePath)) {
                ScanUtils.scanJar(src)
            }
            FileUtils.copyFile(src, dest)
        }
    }

    private void generateClass() {
        if (fileCenterClass) {
        }
    }
}