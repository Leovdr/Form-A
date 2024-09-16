import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ItemsViewModel(val image: Int, val text: String) : Parcelable
