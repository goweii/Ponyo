package per.goweii.ponyo.startup.gradle.plugin

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.FileUtils
import org.gradle.api.Project

class RegisterTransform extends Transform {
    Project project
    static ArrayList<ScanMeta> registerList
    static File fileContainsInitClass;

    RegisterTransform(Project project) {
        this.project = project
    }

    @Override
    String getName() {
        return ScanConst.PLUGIN_NAME
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
    void transform(
            Context context,
            Collection<TransformInput> inputs,
            Collection<TransformInput> referencedInputs,
            TransformOutputProvider outputProvider,
            boolean isIncremental
    ) throws IOException, TransformException, InterruptedException {
        Logger.i('Start scan register info in jar file.')
        long startTime = System.currentTimeMillis()
        boolean leftSlash = File.separator == '/'
        inputs.each { TransformInput input ->
            // scan all jars
            input.jarInputs.each { JarInput jarInput ->
                String destName = jarInput.name
                // rename jar files
                def hexName = DigestUtils.md5Hex(jarInput.file.absolutePath)
                if (destName.endsWith(".jar")) {
                    destName = destName.substring(0, destName.length() - 4)
                }
                // input file
                File src = jarInput.file
                // output file
                File dest = outputProvider.getContentLocation(destName + "_" + hexName, jarInput.contentTypes, jarInput.scopes, Format.JAR)

                //scan jar file to find classes
                if (ScanUtils.shouldProcessPreDexJar(src.absolutePath)) {
                    ScanUtils.scanJar(src, dest)
                }
                FileUtils.copyFile(src, dest)

            }
            // scan class files
            input.directoryInputs.each { DirectoryInput directoryInput ->
                File dest = outputProvider.getContentLocation(directoryInput.name, directoryInput.contentTypes, directoryInput.scopes, Format.DIRECTORY)
                String root = directoryInput.file.absolutePath
                if (!root.endsWith(File.separator))
                    root += File.separator
                directoryInput.file.eachFileRecurse { File file ->
                    def path = file.absolutePath.replace(root, '')
                    if (!leftSlash) {
                        path = path.replaceAll("\\\\", "/")
                    }
                    if(file.isFile() && ScanUtils.shouldProcessClass(path)){
                        ScanUtils.scanClass(file)
                    }
                }

                // copy to dest
                FileUtils.copyDirectory(directoryInput.file, dest)
            }
        }

        Logger.i('Scan finish, current cost time ' + (System.currentTimeMillis() - startTime) + "ms")

        if (fileContainsInitClass) {
            registerList.each { ext ->
                Logger.i('Insert register code to file ' + fileContainsInitClass.absolutePath)

                if (ext.classList.isEmpty()) {
                    Logger.e("No class implements found for interface:" + ext.interfaceName)
                } else {
                    ext.classList.each {
                        Logger.i(it)
                    }
                    RegisterCodeGenerator.insertInitCodeTo(ext)
                }
            }
        }

        Logger.i("Generate code finish, current cost time: " + (System.currentTimeMillis() - startTime) + "ms")
    }
}