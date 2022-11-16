package ni.edu.uca.mini_proyecto

import androidx.documentfile.provider.DocumentFile




/**
 * Versión de almacenamiento en caché de un [DocumentFile].
 *
 * Un [DocumentFile] realizará una búsqueda (a través del sistema [ContentResolver]), cada vez que un
 * se hace referencia a la propiedad. Esto significa que una solicitud de [DocumentFile.getName] es mucho *
 * más lento de lo que cabría esperar.
 *
 * Para mejorar el rendimiento en la aplicación, donde queremos poder ordenar una lista de [DocumentFile]
 * por nombre, lo envolvemos así para que el valor solo se busque una vez.
 */

data class CachingDocumentFile(private val documentFile: DocumentFile) {
    val name: String? by lazy { documentFile.name }
    val type: String? by lazy { documentFile.type }

    val isDirectory: Boolean by lazy { documentFile.isDirectory }

    val uri get() = documentFile.uri

    fun rename(newName: String): CachingDocumentFile {
        documentFile.renameTo(newName)
        return CachingDocumentFile(documentFile)
    }
}

fun Array<DocumentFile>.toCachingList(): List<CachingDocumentFile> {
    val list = mutableListOf<CachingDocumentFile>()
    for (document in this) {
        list += CachingDocumentFile(document)
    }
    return list
}