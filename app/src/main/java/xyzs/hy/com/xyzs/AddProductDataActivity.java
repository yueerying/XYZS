package xyzs.hy.com.xyzs;

import android.content.*;
import android.net.*;
import android.os.*;
import android.provider.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;

import java.io.*;

import android.graphics.*;

import cn.bmob.v3.listener.*;

import android.text.*;

import cn.bmob.v3.datatype.*;
import xyzs.hy.com.xyzs.common.IsPhone;
import xyzs.hy.com.xyzs.entity.Found;

import android.database.*;
import android.app.*;

import cn.bmob.v3.*;
import xyzs.hy.com.xyzs.entity.*;

/**
 * 添加商品
 */
public class AddProductDataActivity extends Activity implements OnClickListener, TextWatcher {

    public static final int TAKE_PHOTO = 1;
    public static final int CROP_PHOTO = 2;
    private String imagePath = null;
    private Bitmap bitmap;

    private EditText titleEdittext;
    private EditText phoneEdittext;
    private EditText describeEdittext;

    private Uri imageUri;

    private ImageView image;

    private Context mContext;

    private Button finish;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_product_datas);
        mContext = AddProductDataActivity.this;
        initLayout();
    }

    //初始化控件
    private void initLayout() {
        titleEdittext = (EditText) findViewById(R.id.add_found_title);
        phoneEdittext = (EditText) findViewById(R.id.add_found_phone);
        describeEdittext = (EditText) findViewById(R.id.add_found_describe);
        titleEdittext.addTextChangedListener(this);
        phoneEdittext.addTextChangedListener(this);
        describeEdittext.addTextChangedListener(this);
        image = (ImageView) findViewById(R.id.add_found_imageview);
        finish = (Button) findViewById(R.id.add_found_finish);
        image.setOnClickListener(this);
        finish.setOnClickListener(this);
    }

    //上传数据
    private void upDatas() {
        final String phone, title, describe;
        final User user = BmobUser.getCurrentUser(this, User.class);
        title = titleEdittext.getText().toString();
        phone = phoneEdittext.getText().toString();
        describe = describeEdittext.getText().toString();
        if (title.equals("") || phone.equals("") || describe.equals("")) {
            Toast.makeText(getApplication(), "请完善信息...", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!IsPhone.isPhone(phone)) {
            Toast.makeText(getApplication(), "输入正确的手机号", Toast.LENGTH_SHORT).show();
            return;
        }

        if (imagePath == null) {
            Product product = new Product(title, describe, phone, null, 0, user);
            product.save(this, new SaveListener() {
                @Override
                public void onSuccess() {
                    Intent intent = new Intent(getApplication(), ProductActivity.class);
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onFailure(int p1, String p2) {
                }
            });
        } else {
            final BmobFile bmobFile = new BmobFile(new File(imagePath));
            bmobFile.uploadblock(mContext, new UploadFileListener() {
                @Override
                public void onSuccess() {
                    String image = bmobFile.getFileUrl(mContext);
                    Product product = new Product(title, describe, phone, image, 1, user);
                    product.save(mContext, new SaveListener() {
                        @Override
                        public void onSuccess() {
                            Toast.makeText(getApplicationContext(), "发布成功！", Toast.LENGTH_SHORT)
                                    .show();
                            Intent intent = new Intent(getApplication(), ProductActivity.class);
                            startActivity(intent);
                            finish();
                        }

                        @Override
                        public void onFailure(int p1, String p2) {
                        }
                    });
                }

                @Override
                public void onFailure(int p1, String p2) {
                }
            });
        }
    }

    //监听
    @Override
    public void onClick(View p1) {
        switch (p1.getId()) {
            case R.id.add_found_imageview:
                // 创建File对象，用于存储选择的照片
                File outputImage = new File(Environment.
                        getExternalStorageDirectory(), "tempImage.jpg");
                try {
                    if (outputImage.exists()) {
                        outputImage.delete();
                    }
                    outputImage.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                imageUri = Uri.fromFile(outputImage);
                Intent intent = new Intent("android.intent.action.PICK");
                intent.setType("image/*");
                intent.putExtra("crop", true);
                //intent.putExtra("aspectX", 1);
                //intent.putExtra("aspectY", 1);
                intent.putExtra("scale", true);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent, TAKE_PHOTO);
                break;

            case R.id.add_found_finish:
                upDatas();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    if (data != null) {
                        imageUri = data.getData();
                    }
                    Intent intent = new Intent("com.android.camera.action.CROP");
                    intent.setDataAndType(imageUri, "image/*");
                    //intent.putExtra("aspectX", 1);
                    //intent.putExtra("aspectY", 1);
                    intent.putExtra("scale", true);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    startActivityForResult(intent, CROP_PHOTO); // 启动裁剪程序
                }
                break;
            case CROP_PHOTO:
                if (resultCode == RESULT_OK) {
                    try {
                        bitmap = BitmapFactory.decodeStream
                                (getContentResolver()
                                        .openInputStream(imageUri));
                        image.setImageBitmap(bitmap); // 将裁剪后的照片显示出
                        Uri uri;
                        if (data.getData() == null) {
                            uri = imageUri;
                        } else {
                            uri = data.getData();
                        }
                        try {
                            String[] pojo = {MediaStore.Images.Media.DATA};
                            Cursor cursor = getContentResolver().query(uri, pojo, null, null, null);
                            ContentResolver cv = this.getContentResolver();
                            int colunm_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                            cursor.moveToFirst();
                            imagePath = cursor.getString(colunm_index);
                        } catch (Exception e) {

                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
            default:
                break;
        }
    }

    //Button状态
    @Override
    public void beforeTextChanged(CharSequence p1, int p2, int p3, int p4) {
    }

    @Override
    public void onTextChanged(CharSequence p1, int p2, int p3, int p4) {
    }

    @Override
    public void afterTextChanged(Editable p1) {
        if (titleEdittext.getText().toString() != null &&
                describeEdittext.getText().toString() != null &&
                phoneEdittext.getText().toString() != null) {
            finish.setEnabled(true);
            finish.setTextColor(Color.BLACK);
        } else {
            finish.setEnabled(false);
        }
    }
}
