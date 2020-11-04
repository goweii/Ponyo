package per.goweii.ponyo.startup.gradle.plugin

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import org.gradle.api.Project

class StartupTransform extends Transform {
    Project project
    static ArrayList<ScanMeta> registerList
    static File fileContainsInitClass;

    StartupTransform(Project project) {
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
    void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        super.transform(transformInvocation)

    }

    @Override
    void transform(
            Context context,
            Collection<TransformInput> inputs,
            Collection<TransformInput> referencedInputs,
            TransformOutputProvider outputProvider,
            boolean isIncremental
    ) throws IOException, TransformException, InterruptedException {
        inputs.each { TransformInput input ->
            input.directoryInputs.each { DirectoryInput directoryInput ->
                String root = directoryInput.file.absolutePath
                if (!root.endsWith(File.separator)){
                    root += File.separator
                }
                directoryInput.file.eachFileRecurse { File file ->
                    if(file.isFile()){
                        def path = file.absolutePath.replace(root, '')
                        ScanUtils.scanClass(file, path)
                    }
                }
            }
            input.jarInputs.each { JarInput jarInput ->
                File file = jarInput.file
                ScanUtils.scanJar(file)
            }
        }
    }
}