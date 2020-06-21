package per.goweii.ponyo.startup.compiler

import com.google.auto.service.AutoService
import com.squareup.javapoet.*
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
        if (annotations.isNullOrEmpty()) return true
        roundEnvironment ?: return true
        val elements = roundEnvironment.getElementsAnnotatedWith(Startup::class.java) ?: return true
        for (element in elements) {
            if (!element.kind.isClass) continue
            val initializerTM: TypeMirror =
                elementUtils.getTypeElement("per.goweii.ponyo.startup.Initializer").asType()
            if (!typeUtils.isSubtype(element.asType(), initializerTM)) {
                printError("StartupProcessor->", "必须实现Initializer接口")
                continue
            }
            element as TypeElement
            val qualifiedName = element.qualifiedName.toString()
            printInfo("qualifiedName->", qualifiedName)
            initializerClasses.add(qualifiedName)
        }
        writeFile()
        return true
    }

    private val initializerClasses = arrayListOf<String>()

    private fun printInfo(prefix: String, msg: CharSequence) {
        messager.printMessage(Diagnostic.Kind.NOTE, "$prefix$msg")
    }

    private fun printError(prefix: String, msg: CharSequence) {
        messager.printMessage(Diagnostic.Kind.ERROR, "$prefix$msg")
    }

    private fun writeFile() {
        val fieldType = FieldSpec.builder(ArrayList::class.java, "initializers")
            .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
            .build()
        val constructorStr = StringBuilder()
        constructorStr.append("initializers = new ArrayList<per.goweii.ponyo.startup.Initializer>();\n")
        constructorStr.append("try {\n")
        initializerClasses.forEach {
            constructorStr.append("  initializers.add(Class.forName(\"$it\").newInstance());\n")
        }
        constructorStr.append("} catch (java.lang.Exception e) {\n")
        constructorStr.append("}\n")
        val constructorMethod = MethodSpec.constructorBuilder()
            .addModifiers(Modifier.PUBLIC)
            .addCode(CodeBlock.of(constructorStr.toString()))
            .build()
        val classType = TypeSpec.classBuilder("StartupHolder")
            .addModifiers(Modifier.PUBLIC)
            .addField(fieldType)
            .addMethod(constructorMethod)
            .build()
        val file = JavaFile.builder("per.goweii.ponyo.startup", classType)
            .build()
        try {
            file.writeTo(filer)
        } catch (e: Exception) {
        }
    }
}