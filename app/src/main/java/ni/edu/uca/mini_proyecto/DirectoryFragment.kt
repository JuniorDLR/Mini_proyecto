package ni.edu.uca.mini_proyecto

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView




/**
 * Fragmento que muestra una lista de documentos en un directorio.
 */

class DirectoryFragment : Fragment() {
    private lateinit var directoryUri: Uri

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: DirectoryEntryAdapter

    private lateinit var viewModel: DirectoryFragmentViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        directoryUri = arguments?.getString(ARG_DIRECTORY_URI)?.toUri()
            ?: throw IllegalArgumentException("Debe pasar la URI del directorio para abrir")



        viewModel = ViewModelProvider(this)
            .get(DirectoryFragmentViewModel::class.java)

        val view = inflater.inflate(R.layout.fragment_directory, container, false)
        recyclerView = view.findViewById(R.id.list)
        recyclerView.layoutManager = LinearLayoutManager(recyclerView.context)

        adapter = DirectoryEntryAdapter(object : ClickListeners {
            override fun onDocumentClicked(clickedDocument: CachingDocumentFile) {
                viewModel.documentClicked(clickedDocument)
            }

            override fun onDocumentLongClicked(clickedDocument: CachingDocumentFile) {
                renameDocument(clickedDocument)
            }
        })

        recyclerView.adapter = adapter

        viewModel.documents.observe(viewLifecycleOwner, Observer { documents ->
            documents?.let { adapter.setEntries(documents) }
        })

        viewModel.openDirectory.observe(viewLifecycleOwner, Observer { event ->
            event.getContentIfNotHandled()?.let { directory ->
                (activity as? MainActivity)?.showDirectoryContents(directory.uri)
            }
        })

        viewModel.openDocument.observe(viewLifecycleOwner, Observer { event ->
            event.getContentIfNotHandled()?.let { document ->
                openDocument(document)
            }
        })

        return view
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        viewModel.loadDirectory(directoryUri)
    }

    private fun openDocument(document: CachingDocumentFile) {
        try {
            val openIntent = Intent(Intent.ACTION_VIEW).apply {
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                data = document.uri
            }
            startActivity(openIntent)
        } catch (ex: ActivityNotFoundException) {
            Toast.makeText(
                requireContext(),
                resources.getString(R.string.error_no_activity, document.name),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    @SuppressLint("InflateParams")
    private fun renameDocument(document: CachingDocumentFile) {
        // Normalmente no queremos pasar `null` como padre, pero el diálogo no existe,
        // por lo que aún no hay un diseño principal para usar.
        val dialogView = layoutInflater.inflate(R.layout.rename_layout, null)
        val editText = dialogView.findViewById<EditText>(R.id.file_name)
        editText.setText(document.name)

         // Usa una lambda para que tengamos acceso al [EditText] con el nuevo nombre.
        val buttonCallback: (DialogInterface, Int) -> Unit = { _, buttonId ->
            when (buttonId) {
                DialogInterface.BUTTON_POSITIVE -> {
                    val newName = editText.text.toString()
                    if (newName.isNotBlank()) {
                        document.rename(newName)

                        // La forma más fácil de actualizar la interfaz de usuario es cargar el directorio nuevamente.
                        viewModel.loadDirectory(directoryUri)
                    }
                }
            }
        }

        val renameDialog = AlertDialog.Builder(requireActivity())
            .setTitle(R.string.rename_title)
            .setView(dialogView)
            .setPositiveButton(R.string.rename_okay, buttonCallback)
            .setNegativeButton(R.string.rename_cancel, buttonCallback)
            .create()
// Cuando se muestre el cuadro de diálogo, seleccione el nombre para que pueda cambiarse fácilmente.
        renameDialog.setOnShowListener {
            editText.requestFocus()
            editText.selectAll()
        }

        renameDialog.show()
    }

    companion object {


        @JvmStatic
        fun newInstance(directoryUri: Uri) =
            DirectoryFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_DIRECTORY_URI, directoryUri.toString())
                }
            }
    }
}

private const val ARG_DIRECTORY_URI = "com.example.android.directoryselection.ARG_DIRECTORY_URI"
