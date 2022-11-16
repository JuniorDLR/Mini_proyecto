package ni.edu.uca.mini_proyecto

import androidx.documentfile.provider.DocumentFile

//Ofrece una vista simplificada de un árbol de documentos

data class CachingDocumentFile(private val documentFile: DocumentFile) {
    val name: String? by lazy { documentFile.name } //nombre del nuevo documento
    val type: String? by lazy { documentFile.type }  //Tipo de documento nuevo, como archivo pdf

    val isDirectory: Boolean by lazy { documentFile.isDirectory } //Crea, almacenar en un nuevo directorio

    val uri get() = documentFile.uri /*identificador de recurso uniforme,
Crea un DocumentFile que represente el documento único en el Uri dado.*/

    fun rename(newName: String): CachingDocumentFile { //renombar directorio
        documentFile.renameTo(newName)
        return CachingDocumentFile(documentFile)
    }
}

fun Array<DocumentFile>.toCachingList(): List<CachingDocumentFile> { //una matriz de archivos, creando documentos.
    val list = mutableListOf<CachingDocumentFile>()
    for (document in this) {
        list += CachingDocumentFile(document)
    }
    return list
}