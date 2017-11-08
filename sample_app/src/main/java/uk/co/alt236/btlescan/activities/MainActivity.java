package uk.co.alt236.btlescan.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.concurrent.CopyOnWriteArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import uk.co.alt236.bluetoothlelib.device.BluetoothLeDevice;
import uk.co.alt236.bluetoothlelib.device.beacon.BeaconType;
import uk.co.alt236.bluetoothlelib.device.beacon.BeaconUtils;
import uk.co.alt236.btlescan.R;
import uk.co.alt236.btlescan.adapters.LeDeviceListAdapter;
import uk.co.alt236.btlescan.containers.BluetoothLeDeviceStore;
import uk.co.alt236.btlescan.util.BluetoothLeScanner;
import uk.co.alt236.btlescan.util.BluetoothUtils;
import uk.co.alt236.easycursor.objectcursor.EasyObjectCursor;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    @Bind(R.id.tvBluetoothLe)
    protected TextView mTvBluetoothLeStatus;
    @Bind(R.id.tvBluetoothStatus)
    protected TextView mTvBluetoothStatus;
    @Bind(R.id.tvItemCount)
    protected TextView mTvItemCount;
    @Bind(android.R.id.list)
    protected ListView mList;
    @Bind(android.R.id.empty)
    protected View mEmpty;

    private BluetoothUtils mBluetoothUtils;
    private BluetoothLeScanner mScanner;
    private LeDeviceListAdapter mLeDeviceListAdapter;
    private BluetoothLeDeviceStore mDeviceStore;
    private static final int REQUEST_ENABLE_BT = 1;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 100;


//    public String StreamToStr(InputStream is) {
//        try {
//            // 定义字节数组输出流对象
//            ByteArrayOutputStream os = new ByteArrayOutputStream();
//            // 定义读取的长度
//            int len = 0;
//            // 定义读取的缓冲区
//            byte buffer[] = new byte[1024];
//            // 按照定义的缓冲区进行循环读取，直到读取完毕为止
//            while ((len = is.read(buffer)) != -1) {
//                // 根据读取的长度写入到字节数组输出流对象中
//                os.write(buffer, 0, len);
//            }
//            // 关闭流
//            is.close();
//            os.close();
//            // 把读取的字节数组输出流对象转换成字节数组
//            byte data[] = os.toByteArray();
//            // 按照指定的编码进行转换成字符串(此编码要与服务端的编码一致就不会出现乱码问题了，android默认的编码为UTF-8)
//            return new String(data, "UTF-8");
//        } catch (IOException e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
//    /**
//     * HttpClient中POST方式的处理
//     */
//    public void DoPost(String deviceName) {
//        //1.创建 HttpClient 的实例
//        HttpClient client = new DefaultHttpClient();
//        //2. 创建某种连接方法的实例，在这里是HttpPost。在 HttpPost 的构造函数中传入待连接的地址
//        String uri="http://172.16.237.200:8080/video/login.do";
//        HttpPost httpPost = new HttpPost(uri);
//        try {
//            //封装传递参数的集合
//            List<NameValuePair> parameters = new ArrayList<NameValuePair>();
//            //往这个集合中添加你要传递的参数
//            parameters.add(new BasicNameValuePair("device", deviceName));
//            //创建传递参数封装 实体对象
//            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(parameters, "UTF-8");//设置传递参数的编码
//            //把实体对象存入到httpPost对象中
//            httpPost.setEntity(entity);
//            //3. 调用第一步中创建好的实例的 execute 方法来执行第二步中创建好的 method 实例
//            HttpResponse response = client.execute(httpPost); //HttpUriRequest的后代对象 //在浏览器中敲一下回车
//            //4. 读 response
//            if(response.getStatusLine().getStatusCode()==200){//判断状态码
//                InputStream is = response.getEntity().getContent();//获取内容
//                final String result = StreamToStr(is); // 通过工具类转换文本
//            }
//        } catch (Exception e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }finally{
//            //6. 释放连接。无论执行方法是否成功，都必须释放连接
//            client.getConnectionManager().shutdown();
//        }
//    }
//
//    /**
//     * 通过httpClient中的GET方式处理的
//     */
//    public void DoGet(){
//        // HttpClient 发请求 GET方式处理
//        // 1.创建 HttpClient 的实例 打开一个浏览器
//        HttpClient client = new DefaultHttpClient(); // DefaultHttpClient extends AbstractHttpClient
//        try {
//
//            // 2. 创建某种连接方法的实例，在这里是HttpGet。在 HttpGet
//            // 的构造函数中传入待连接的地址
////            String uri = "http://172.16.237.200:8080/video/login.do?username="
////                    + userName + "&userpass=" + userPass;
//
//            String uri = "https://wwww.baidu.com";
//
//            //强调 地址不能够出现 localhost:操作
//            HttpGet httpGet = new HttpGet(uri);
//            // 3. 调用第一步中创建好的实例的 execute 方法来执行第二步中创建好的 method 实例
//            HttpResponse response = client.execute(httpGet); // 在浏览器中敲了一下回车
//            // 4. 读 response
//            int statusCode = response.getStatusLine()
//                    .getStatusCode();// 读取状态行中的状态码
//            if (statusCode == 200) { //如果等于200 一切ok
//                HttpEntity entity = response.getEntity();// 返回实体对象
//                InputStream is = entity.getContent(); // 读取实体中内容
//                final String result = StreamToStr(is); // 通过工具类转换文本;
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            // 5.释放连接。无论执行方法是否成功，都必须释放连接
//            client.getConnectionManager().shutdown();// 释放链接
//        }
//    }




//    private void getAsynHttp() {
//        OkHttpClient mOkHttpClient=new OkHttpClient();
//        Request.Builder requestBuilder = new Request.Builder().url("http://www.baidu.com");
//        //可以省略，默认是GET请求
//        requestBuilder.method("GET",null);
//        Request request = requestBuilder.build();
//        Call mcall= mOkHttpClient.newCall(request);
//        mcall.enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                if (null != response.cacheResponse()) {
//                    String str = response.cacheResponse().toString();
//                    Log.i("BLE.......", "cache---" + str);
//                } else {
//                    response.body().string();
//                    String str = response.networkResponse().toString();
//                    Log.i("BLE.......", "network---" + str);
//                }
//            }
//        });
//    }

    private int duration = 1000*20;
    private Timer timer;
    private void startScan() {

        //CrashReport.testJavaCrash();

        final boolean mIsBluetoothOn = mBluetoothUtils.isBluetoothOn();
        final boolean mIsBluetoothLePresent = mBluetoothUtils.isBluetoothLeSupported();
        mDeviceStore.clear();
        updateItemCount(0);

        mLeDeviceListAdapter = new LeDeviceListAdapter(this, mDeviceStore.getDeviceCursor());
        mList.setAdapter(mLeDeviceListAdapter);

        mBluetoothUtils.askUserToEnableBluetoothIfNeeded();
        if (mIsBluetoothOn && mIsBluetoothLePresent) {
            mScanner.scanLeDevice(-1, true);
            invalidateOptionsMenu();
        }

        timer=new Timer();
        timer.schedule(new java.util.TimerTask() {

            @Override
            public void run() {
                Log.i("BLE.......", "timer arrived");
                if (deviceList.size() > 0){
//                    try {
//                        Log.i("BLE.......", "上报数据");
//                        postAsynHttp();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
                }
            }
        }, duration, duration);
    }

    public class WatchData {
        private List<String> tags;

        public List<String> getTags() {
            return tags;
        }

        public void setTags(List<String> tags) {
            this.tags = tags;
        }


    }

    private void postAsynHttp() throws IOException {
        OkHttpClient client = new OkHttpClient();
        MediaType JSON
                = MediaType.parse("application/json; charset=utf-8");

        Gson gson = new Gson();
        WatchData devices = new WatchData();
        devices.setTags(deviceList);
        //将对象转换为JSON数据
        String jsonData = gson.toJson(devices);
        jsonData = "{\"WatchData\":" + jsonData + "}";

        RequestBody body = RequestBody.create(JSON, jsonData);
        Request request = new Request.Builder()
                .url("http://122.192.0.170:8001/edit/services/rest/edit/WatchData")
                .post(body)
                .build();

        Call mcall= client.newCall(request);
        mcall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("BLE.......", "上报数据失败");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (null != response.cacheResponse()) {
                    String str = response.cacheResponse().toString();
                    Log.i("BLE.......", "cache---" + str);
                } else {
                    response.body().string();
                    String str = response.networkResponse().toString();
                    Log.i("BLE.......", "network---" + str);
                }
            }
        });


//        OkHttpClient mOkHttpClient=new OkHttpClient();
//        RequestBody formBody = new FormBody.Builder()
//                .add("size", "10")
//                .build();
//        Request request = new Request.Builder()
//                .url("http://api.1-blog.com/biz/bizserver/article/list.do")
//                .post(formBody)
//                .build();
//        Call call = mOkHttpClient.newCall(request);
//        call.enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                String str = response.body().string();
//                Log.i("haohan", str);
//            }
//
//        });
    }

    private List<String> deviceList = new CopyOnWriteArrayList<>();

    private final BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi, final byte[] scanRecord) {

            final BluetoothLeDevice deviceLe = new BluetoothLeDevice(device, rssi, scanRecord, System.currentTimeMillis());

            // feed the device list
            String myname = deviceLe.getAddress();
            String n3 = myname.substring(15, 17);
            String n2 = myname.substring(12, 14);
            String n1 = myname.substring(9,11);
            int num1 = Integer.parseInt(n1, 16);
            int num2= Integer.parseInt(n2, 16);
            int num3 = Integer.parseInt(n3, 16);
            int num = 0;
            num = num1 << 16 | num2 << 8 | num3;
            String strNum = String.valueOf(num);
            String strMac = myname.substring(0, 2);

//            if( strMac.equals("FF")){
                            if (BeaconUtils.getBeaconType(deviceLe) == BeaconType.IBEACON){
                {
                    deviceList.add(String.valueOf(num));

                    mDeviceStore.addDevice(deviceLe);
                    final EasyObjectCursor<BluetoothLeDevice> c = mDeviceStore.getDeviceCursor();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mLeDeviceListAdapter.swapCursor(c);
                            updateItemCount(mLeDeviceListAdapter.getCount());
                        }
                    });
                }
            }

//            deviceList.add(String.valueOf(num));
//
//
//            final EasyObjectCursor<BluetoothLeDevice> c = mDeviceStore.getDeviceCursor();
//
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    mLeDeviceListAdapter.swapCursor(c);
//                    updateItemCount(mLeDeviceListAdapter.getCount());
//                }
//            });
        }
    };

    private void displayAboutDialog() {
        // REALLY REALLY LAZY LINKIFIED DIALOG
        final int paddingSizeDp = 20;
        final float scale = getResources().getDisplayMetrics().density;
        final int dpAsPixels = (int) (paddingSizeDp * scale + 0.5f);

        final TextView textView = new TextView(this);
        final SpannableString text = new SpannableString(getString(R.string.about_dialog_text));

        textView.setText(text);
        textView.setAutoLinkMask(RESULT_OK);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        textView.setPadding(dpAsPixels, dpAsPixels, dpAsPixels, dpAsPixels);
        textView.setGravity(Gravity.CENTER);
        Linkify.addLinks(text, Linkify.ALL);
        new AlertDialog.Builder(this)
                .setTitle(R.string.menu_about)
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                    }
                })
                .setView(textView)
                .show();
    }

    private void askBle(){
        // 检查当前手机是否支持ble 蓝牙,如果不支持退出程序
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
//            finish();
        }
        if (mBluetoothUtils.getBluetoothAdapter() == null || !mBluetoothUtils.getBluetoothAdapter().isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    /*
  校验蓝牙权限
 */
    private void checkBluetoothPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            //校验是否已具有模糊定位权限
            if (ContextCompat.checkSelfPermission(MainActivity.this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
            } else {
                //具有权限
//                startScan();
            }
        } else {
            //系统不高于6.0直接执行
//            startScan();
        }
    }


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mList.setEmptyView(mEmpty);
        mList.setOnItemClickListener(this);
        mDeviceStore = new BluetoothLeDeviceStore();
        mBluetoothUtils = new BluetoothUtils(this);
        mScanner = new BluetoothLeScanner(mLeScanCallback, mBluetoothUtils);
        updateItemCount(0);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);   //应用运行时，保持屏幕高亮，不锁屏

        //startScan();
        askBle();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_ENABLE_BT:
                if (resultCode == RESULT_OK) {
                    Toast.makeText(this, "蓝牙已启用", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "蓝牙未启用", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        if (!mScanner.isScanning()) {
            menu.findItem(R.id.menu_stop).setVisible(false);
            menu.findItem(R.id.menu_scan).setVisible(true);
            menu.findItem(R.id.menu_refresh).setActionView(null);
        } else {
            menu.findItem(R.id.menu_stop).setVisible(true);
            menu.findItem(R.id.menu_scan).setVisible(false);
            menu.findItem(R.id.menu_refresh).setActionView(R.layout.actionbar_progress_indeterminate);
        }

        //if (mList.getCount() > 0) {
        //    menu.findItem(R.id.menu_share).setVisible(true);
        //} else {
        menu.findItem(R.id.menu_share).setVisible(false);
        //}

        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        final BluetoothLeDevice device = mLeDeviceListAdapter.getItem(position);
        if (device == null) return;

        final Intent intent = new Intent(this, DeviceDetailsActivity.class);
        intent.putExtra(DeviceDetailsActivity.EXTRA_DEVICE, device);

        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_scan:
                startScan();
                break;
            case R.id.menu_stop:
                stopScan();
                break;
            case R.id.menu_about:
                displayAboutDialog();
                timer.cancel();
                break;
            case R.id.menu_share:
                mDeviceStore.shareDataAsEmail(this);
        }
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mScanner != null){
            mScanner.scanLeDevice(-1, false);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        final boolean mIsBluetoothOn = mBluetoothUtils.isBluetoothOn();
        final boolean mIsBluetoothLePresent = mBluetoothUtils.isBluetoothLeSupported();

        if (mIsBluetoothOn) {
            mTvBluetoothStatus.setText(R.string.on);
        } else {
            mTvBluetoothStatus.setText(R.string.off);
        }

        if (mIsBluetoothLePresent) {
            mTvBluetoothLeStatus.setText(R.string.supported);
        } else {
            mTvBluetoothLeStatus.setText(R.string.not_supported);
        }

        invalidateOptionsMenu();

        checkBluetoothPermission();
    }

    private void stopScan(){
        timer.cancel();
        mScanner.scanLeDevice(-1, false);
        invalidateOptionsMenu();

        deviceList.clear();
    }

    private void updateItemCount(final int count) {
        mTvItemCount.setText(
                getString(
                        R.string.formatter_item_count,
                        String.valueOf(count)));
    }

}
