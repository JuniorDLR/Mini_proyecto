package ni.edu.uca.mini_proyecto

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class DirectoryEntryAdapter (
    private val clickListeners: ClickListeners
) : RecyclerView.Adapter<DirectoryEntryAdapter.ViewHolder>()  { //cargar vista

    private val directoryEntries = mutableListOf<CachingDocumentFile>() //vista de los documentos

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.directory_item, parent, false)
        return ViewHolder(view) //obtener una colección de archivos del documento.
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        with(viewHolder) {
            val item = directoryEntries[position]
            val itemDrawableRes = if (item.isDirectory) {
                R.drawable.ic_folder_black_24dp
            } else {
                R.drawable.ic_file_black_24dp
            }

            fileName.text = item.name  //lo que se guarda en nombre y tipo
            mimeType.text = item.type ?: ""
            imageView.setImageResource(itemDrawableRes)

            root.setOnClickListener {
                clickListeners.onDocumentClicked(item)
            }
            root.setOnLongClickListener {
                clickListeners.onDocumentLongClicked(item)
                true
            }
        }
    }

    override fun getItemCount() = directoryEntries.size //cantidad de directorios

    fun setEntries(newList: List<CachingDocumentFile>) { //se anaden el direcotrio
        synchronized(directoryEntries) {
            directoryEntries.clear()
            directoryEntries.addAll(newList)
            notifyDataSetChanged()
        }
    }
    /**
     * Provide a reference to the type of views that you are using (custom ViewHolder)
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val root = view
        val fileName: TextView = view.findViewById(R.id.file_name)
        val mimeType: TextView = view.findViewById(R.id.mime_type)
        val imageView: ImageView = view.findViewById(R.id.entry_image)
    }
}

interface ClickListeners {
    fun onDocumentClicked(clickedDocument: CachingDocumentFile)
    fun onDocumentLongClicked(clickedDocument: CachingDocumentFile)
}
