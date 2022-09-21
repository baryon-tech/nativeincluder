package io.github.baryontech.nativeincluder.processor

import RequestedFieldType
import com.google.devtools.ksp.getClassDeclarationByName
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.*
import com.squareup.kotlinpoet.ksp.KotlinPoetKspPreview
import com.squareup.kotlinpoet.ksp.toClassName
import io.github.baryontech.nativeincluder.annotations.IncludeFile
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.OutputStream
import java.nio.charset.Charset
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.absolutePathString

@OptIn(KotlinPoetKspPreview::class)
class IncludeProcessor(val environment: SymbolProcessorEnvironment) : SymbolProcessor {
    override fun process(resolver: Resolver): List<KSAnnotated> {
        var files = resolver.getSymbolsWithAnnotation(IncludeFile::class.qualifiedName!!).toList();
        for (file in files) {
            var annos = file.annotations.filter {
                it.annotationType
                    .resolve()
                    .declaration.qualifiedName?.asString() == IncludeFile::class.qualifiedName
            };
            var clazz = (file as KSClassDeclaration)


            var objName = clazz.simpleName.getShortName() + "Files";
            var pkgName = if (clazz.packageName.asString() != "") clazz.packageName.asString() + ".generated" else "";
            val file: OutputStream = environment.codeGenerator.createNewFile(
                // Make sure to associate the generated file with sources to keep/maintain it across incremental builds.
                // Learn more about incremental processing in KSP from the official docs:
                // https://kotlinlang.org/docs/ksp-incremental.html
                dependencies = Dependencies(false, *resolver.getAllFiles().toList().toTypedArray()),
                packageName = pkgName,
                fileName = objName
            )
            val writer = file.writer(Charset.defaultCharset());
            if (pkgName != "")
                writer.write("package $pkgName;")
            writer.write("object $objName {")
            for (anno in annos) {
                var source = anno.arguments.filter { a -> a.name!!.asString().equals("source") }.first().value!! as String;
                var path = anno.arguments.filter { a -> a.name!!.asString().equals("path") }.first().value!! as String;
                var fieldName = anno.arguments.filter { a -> a.name!!.asString().equals("fieldName") }.first().value!! as String;
                var type = anno.arguments.filter { a -> a.name!!.asString().equals("type") }.first().value!! as KSType;

                val file = resolveData(environment, source, path);
                var fileData = FileInputStream(file).readAllBytes();

                when (type.toString()) {
                    "RequestedFieldType.NONE" -> throw Exception("Invalid requested field type.")
                    "RequestedFieldType.STRING" -> {
                        writer.write("val " + fieldName + ": String = \"${
                            fileData.toString(Charset.defaultCharset())
                                .replace("\"", "\\\"")
                                .replace("\n", "\\n")
                                .replace("\r", "\\r")
                        }\";");
                    }
                    "RequestedFieldType.BYTE_ARRAY" -> {
                        writer.write("val " + fieldName + ": UByteArray = ubyteArrayOf(${fileData.map { it.toUByte() }.map { "0x" + it.toString(16) + "u" }.joinToString(",")});")
                    }
                }
            }
            writer.write("};")
            writer.flush();
            file.close();
        }
        return listOf();
    }
}

fun resolveData(data: SymbolProcessorEnvironment, set: String, path: String): File {
    var prefix = "nativeincluder.set.";
    var folders = data.options.filterKeys { a -> a.startsWith(prefix) }.mapKeys { a -> a.key.substring(prefix.length) };
    if(!folders.containsKey(set))
        throw Exception("Set \"$set\" has no path specified. Please add a KSP variable called \"nativeincluder.set.$set\" with the appropriate path.")
    var root = folders.get(set);
    var full = Path.of(root, path).absolutePathString();
    var file = File(full);
    if(!file.exists())
        throw Exception("File \"$full\" (set: \"$set\") doesn't exist.")
    return file;
}

inline fun <reified T> KSType.isAssignableFrom(resolver: Resolver): Boolean {
    val classDeclaration = requireNotNull(resolver.getClassDeclarationByName<T>()) {
        "Unable to resolve ${KSClassDeclaration::class.simpleName} for type ${T::class.simpleName}"
    }
    return isAssignableFrom(classDeclaration.asStarProjectedType())
}
