package ni.edu.uca.mini_proyecto

import android.app.Application
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


/**
 * ViewModel para el [DirectoryFragment].
 */

class DirectoryFragmentViewModel(application: Application) : AndroidViewModel(application) {
    private val _documents = MutableLiveData<List<CachingDocumentFile>>()
    val documents = _documents

    private val _openDirectory = MutableLiveData<Event<CachingDocumentFile>>()
    val openDirectory = _openDirectory

    private val _openDocument = MutableLiveData<Event<CachingDocumentFile>>()
    val openDocument = _openDocument

    fun loadDirectory(directoryUri: Uri) {
        val documentsTree = DocumentFile.fromTreeUri(getApplication(), directoryUri) ?: return
        val childDocuments = documentsTree.listFiles().toCachingList()

        // Es mucho mejor cuando los documentos están ordenados por algo, así que ordenaremos los documentos
        // tenemos por nombre. Desafortunadamente, puede haber bastantes documentos, y la clasificación puede llevar
        // algo de tiempo, así que aprovecharemos las corrutinas para eliminar este trabajo del hilo principal.
        viewModelScope.launch {
            val sortedDocuments = withContext(Dispatchers.IO) {
                childDocuments.toMutableList().apply {
                    sortBy { it.name }
                }
            }
            _documents.postValue(sortedDocuments)
        }
    }
    /**
     * Método para enviar entre hacer clic en un documento (que debe estar abierto), y
     * un directorio (al que el usuario quiere navegar).
     */

    fun documentClicked(clickedDocument: CachingDocumentFile) {
        if (clickedDocument.isDirectory) {
            openDirectory.postValue(Event(clickedDocument))
        } else {
            openDocument.postValue(Event(clickedDocument))
        }
    }
}