package per.goweii.ponyo.startup.compiler

import com.google.auto.service.AutoService
import com.squareup.javapoet.*
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.ParameterizedTypeName
import per.goweii.ponyo.startup.annotation.Const
import per.goweii.ponyo.startup.annotation.Startup
import java.util.*
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
            val initializerTM: TypeMirror =
                elementUtils.getTypeElement(Const.INITIALIZER_CLASS_NAME).asType()
            if (!typeUtils.isSubtype(element.asType(), initializerTM)) {
                printError("StartupProcessor->", "必须实现${Const.INITIALIZER_CLASS_NAME}接口")
                continue
            }
            element as TypeElement
            val qualifiedName = element.qualifiedName.toString()
            initializerClasses.add(qualifiedName)
        }
        return writeFile()
    }

    private val initializerClasses = arrayListOf<String>()

    private fun printInfo(prefix: String, msg: CharSequence) {
        messager.printMessage(Diagnostic.Kind.NOTE, "$prefix$msg")
    }

    private fun printError(prefix: String, msg: CharSequence) {
        messager.printMessage(Diagnostic.Kind.ERROR, "$prefix$msg")
    }

    private fun writeFile(): Boolean {
        if (initializerClasses.isEmpty()) {
            return false
        }
        val packageName = initializerClasses.first()
            .toLowerCase(Locale.US)
            .run { substring(0, lastIndexOf(".")) }
            .replace(".", "_")
        val list = ClassName.get("java.util", "ArrayList")
        val stringTypeName = TypeName.get(String::class.java)
        val listOfString: TypeName = ParameterizedTypeName.get(list, stringTypeName)
        val fieldType = FieldSpec.builder(listOfString, Const.GENERATED_LIST_FIELD)
            .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
            .initializer("new \$T()", listOfString)
            .build()
        val constructorStr = StringBuilder()
        initializerClasses.forEach {
            constructorStr.append("${Const.GENERATED_LIST_FIELD}.add(\"$it\");\n")
        }
        val constructorMethod = MethodSpec.constructorBuilder()
            .addModifiers(Modifier.PUBLIC)
            .addCode(CodeBlock.of(constructorStr.toString()))
            .build()
        val classType = TypeSpec.classBuilder(Const.GENERATED_CLASS_NAME)
            .addModifiers(Modifier.PUBLIC)
            .addField(fieldType)
            .addMethod(constructorMethod)
            .build()
        val file = JavaFile.builder("${Const.GENERATED_PACKAGE_NAME}.$packageName", classType)
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