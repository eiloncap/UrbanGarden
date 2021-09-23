//
//import android.R
//import android.app.Activity
//import android.app.ProgressDialog
//import android.content.Intent
//import android.net.Uri
//import android.os.Bundle
//import android.provider.MediaStore
//import android.support.v7.app.AppCompatActivity
//import android.text.TextUtils
//import android.view.View
//import android.widget.Button
//import android.widget.EditText
//import android.widget.ImageButton
//import androidx.appcompat.app.AppCompatActivity
//import com.google.android.gms.tasks.OnSuccessListener
//import com.google.firebase.database.DatabaseReference
//import com.google.firebase.database.FirebaseDatabase
//import com.google.firebase.storage.FirebaseStorage
//import com.google.firebase.storage.StorageReference
//import com.theartofdev.edmodo.cropper.CropImage
//import com.theartofdev.edmodo.cropper.CropImageView
//import il.co.urbangarden.MainActivity
//import java.lang.Exception
//
//class PostActivity : AppCompatActivity() {
//    private var mSelectImage: ImageButton? = null
//    private var mPostTitle: EditText? = null
//    private var mPostDesc: EditText? = null
//    private var mSubmitBtn: Button? = null
//    private var mProgress: ProgressDialog? = null
//    private var mDatabase: DatabaseReference? = null
//    private var mImageUri: Uri? = null
//    private var mStorage: StorageReference? = null
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_post)
//        mStorage = FirebaseStorage.getInstance().reference
//        mDatabase = FirebaseDatabase.getInstance().reference.child("Blog")
//        mSelectImage = findViewById<View>(R.id.imageSelect) as ImageButton
//        mPostTitle = findViewById<View>(R.id.titleField) as EditText
//        mPostDesc = findViewById<View>(R.id.descField) as EditText
//        mSubmitBtn = findViewById<View>(R.id.submitBtn) as Button
//        mProgress = ProgressDialog(this)
//        mSelectImage!!.setOnClickListener {
//            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//            if (intent.resolveActivity(packageManager) != null) {
//                startActivityForResult(intent, CAMERA_REQUEST_CODE)
//            }
//            //startActivityForResult(intent,CAMERA_REQUEST_CODE);
//
//
//            /*
//                            Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
//                            galleryIntent.setType("image/ *");
//                            startActivityForResult(galleryIntent, GALLERY_REQUEST);
//
//                            */
//        }
//        mSubmitBtn!!.setOnClickListener { startPosting() }
//    }
//
//    private fun startPosting() {
//        mProgress!!.setMessage("Posting to blog...")
//        val title_val = mPostTitle!!.text.toString().trim { it <= ' ' }
//        val desc_val = mPostDesc!!.text.toString().trim { it <= ' ' }
//        if (!TextUtils.isEmpty(title_val) && !TextUtils.isEmpty(desc_val) && mImageUri != null) {
//            mProgress!!.show()
//            val filepath = mStorage!!.child("Blog_Images").child(mImageUri!!.lastPathSegment!!)
//            filepath.putFile(mImageUri!!).addOnSuccessListener { taskSnapshot ->
//                val downloadUrl: Uri = taskSnapshot.getDownloadUrl()
//                val newPost = mDatabase!!.push()
//                newPost.child("title").setValue(title_val)
//                newPost.child("desc").setValue(desc_val)
//                newPost.child("image").setValue(downloadUrl.toString())
//                mProgress!!.dismiss()
//                startActivity(Intent(this@PostActivity, MainActivity::class.java))
//            }
//        }
//    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        // if(requestCode == GALLERY_REQUEST && resultCode == RESULT_OK){
//        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
//            mImageUri = data!!.data
//            mSelectImage!!.setImageURI(mImageUri)
//            CropImage.activity(mImageUri)
//                .setGuidelines(CropImageView.Guidelines.ON)
//                .setAspectRatio(1, 1)
//                .start(this)
//
//
//            /* Bitmap mImageUri1 = (Bitmap) data.getExtras().get("data");
//         mSelectImage.setImageBitmap(mImageUri1);
//
//          Toast.makeText(this, "Image saved to:\n" +
//                  data.getExtras().get("data"), Toast.LENGTH_LONG).show();
//
//
//*/
//        }
//        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
//            val result: CropImage.ActivityResult = CropImage.getActivityResult(data)
//            if (resultCode == RESULT_OK) {
//                val resultUri: Uri = result.getUri()
//                mSelectImage!!.setImageURI(resultUri)
//                mImageUri = resultUri
//            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
//                val error: Exception = result.getError()
//            }
//        }
//    }
//
//    companion object {
//        private const val GALLERY_REQUEST = 1
//        private const val CAMERA_REQUEST_CODE = 1
//    }
//}