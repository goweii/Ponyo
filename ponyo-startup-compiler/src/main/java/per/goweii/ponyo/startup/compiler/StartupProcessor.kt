package per.goweii.ponyo.startup.compiler

import com.google.auto.service.AutoService
import com.squareup.javapoet.*
import com.squareup.javapoet.ClassName
import per.goweii.ponyo.startup.annotation.Const
import per.goweii.ponyo.startup.annotation.InitMeta
import per.goweii.ponyo.startup.annotation.Startup
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement
import javax.lang.model.type.TypeMirror
import javax.lang.model.util.Elements
import javax.lang.model.util.Types
import javax.tools.Diagnostic

/**
 * @author CuiZhen
 * @date 2020/6/21
 */
@AutoService(Processor::class)
class StartupProcessor : AbstractProcessor() {
    private lateinit var filer: Filer
    private lateinit var elementUtils: Elements
    private lateinit var typeUtils: Types
    private lateinit var messager: Messager

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return mutableSetOf(
            Startup::class.java.name
        )
    }

    override fun getSupportedSourceVersion(): SourceVersion {
        return SourceVersion.latestSupported()
    }

    override fun init(processingEnvironment: ProcessingEnvironment) {
        super.init(processingEnvironment)
        elementUtils = processingEnv.elementUtils
        typeUtils = processingEnv.typeUtils
        messager = processingEnv.messager
        filer = processingEnv.filer
    }

    override fun process(
        annotations: Set<TypeElement?>?,
        roundEnvironment: RoundEnvironment?
    ): Boolean {
        if (annotations.isNullOrEmpty()) return false
        roundEnvironment ?: return false
        val elements =
            roundEnvironment.getElementsAnnotatedWith(Startup::class.java) ?: return false
        for (element in elements) {
            if (!element.kind.isClass) continue
            val initializerTypeElement = elementUtils.getTypeElement(Const.INITIALIZER_CLASS_NAME)
            val initializerTM: TypeMirror = initializerTypeElement.asType()
            if (!typeUtils.isSubtype(element.asType(), initializerTM)) {
                printError("StartupProcessor->", "mast implement ${Const.INITIALIZER_CLASS_NAME} interface")
                continue
            }
            element as TypeElement
            val qualifiedName = element.qualifiedName.toString()
            val annotation = element.getAnnotation(Startup::class.java)
            val initMeta = InitMeta(qualifiedName, annotation.activities)
            initMetaList.add(initMeta)
        }
        return writeFile()
    }

    private val initMetaList = arrayListOf<InitMeta>()

    private fun printInfo(prefix: String, msg: CharSequence) {
        messager.printMessage(Diagnostic.Kind.NOTE, "$prefix$msg")
    }

    private fun printError(prefix: String, msg: CharSequence) {
        messager.printMessage(Diagnostic.Kind.ERROR, "$prefix$msg")
    }

    private fun writeFile(): Boolean {
        if (initMetaList.isEmpty()) {
            return false
        }
        return try {
            initMetaList.forEach {
                writeFile(it)
            }
            true
        } catch (e: Exception) {
            printError("StartupProcessor->", e.toString())
            false
        }
    }

    @Throws(Exception::class)
    private fun writeFile(meta: InitMeta): Boolean {
        val initMeta = TypeName.get(InitMeta::class.java)
        val fieldType = FieldSpec.builder(initMeta, Const.GENERATED_INIT_META_FIELD)
            .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
            .build()
        val initHolder = ClassName.get(Const.INIT_HOLDER_PACKAGE_NAME, Const.INIT_HOLDER_SIMPLE_CLASS_NAME)
        val constructorStr = StringBuilder()
        constructorStr.append("String className = \"${meta.className}\";")
        constructorStr.append("\nString[] activities = new String[${meta.activities.size}];")
        meta.activities.forEachIndexed { index, activity ->
            constructorStr.append("\nactivities[$index] = \"$activity\";")
        }
        constructorStr.append("\n\$T im = new \$T(className, activities);")
        constructorStr.append("\n${Const.GENERATED_INIT_META_FIELD} = im;\n")
        val constructorMethod = MethodSpec.constructorBuilder()
            .addModifiers(Modifier.PUBLIC)
            .addCode(CodeBlock.of(constructorStr.toString(), initMeta, initMeta))
            .build()
        val getMethod = MethodSpec.methodBuilder(Const.INIT_HOLDER_GET_METHOD_NAME)
            .addModifiers(Modifier.PUBLIC)
            .addAnnotation(Override::class.java)
            .returns(initMeta)
            .addCode(CodeBlock.of("return ${Const.GENERATED_INIT_META_FIELD};"))
            .build()
        val className = meta.className.replace(".", "$")
        val classType = TypeSpec.classBuilder(className)
            .addSuperinterface(initHolder)
            .addModifiers(Modifier.PUBLIC)
            .addField(fieldType)
            .addMethod(constructorMethod)
            .addMethod(getMethod)
            .build()
        val file = JavaFile.builder(Const.GENERATED_PACKAGE_NAME, classType)
            .build()
        return try {
            file.writeTo(filer)
            true
        } catch (e: Exception) {
            printError("StartupProcessor->", e.toString())
            false
        }
    }
}