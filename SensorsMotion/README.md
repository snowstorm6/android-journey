# Overview

- Android hỗ trợ 3 loại cảm biến
    - Motion sensors: Các cảm biến chuyển động. Các cảm biến này giúp đo lực gia tốc và chuyển động quay theo 3 chiều. Bao gồm cảm biến chuyển động (accelerometers), cảm biến trọng lực (gravity sensors), chuyển động quay (gyroscopes), và các cảm biến vector quay (rotational vector sensors)
    - Environmental sensors. Các cảm biến này giúp đo lường các thuộc tính về môi trường như nhiệt độ (temperature) và áp suất(pressure) không khí xung quanh, ánh sáng (illumination) và độ ẩm(humidity). Bao gồm barometers, photometers, and thermometers (*đống này không hiểu nổi)
    - Position sensors. Các cảm biến này giúp đo lường vị trị vật lý của thiết bị. Bao gồm các cảm biến định hướng (orientation) và magnetometers

## Introduction to sensors

- Android sensors framework cung cấp nhiều loại của cảm biến. Một số được xây dựng trên phần cứng và một số trên phần mềm. Các cảm biến xây dựng trên phần cứng là các components vật lý đã được tích hợp sẵn vào thiết bị. Các cảm biến dựa trên phần mềm không phải là những thành phần vật lý mặc dù chúng bắt chước các cảm biến phần cứng. Software-based sensors lấy dữ liệu của chúng từ 1 hoặc nhiều hardware-based sensors hoặc đôi khi được gọi đến các sensors giả lập (virtual sensors) hoặc các cảm biến tổng hợp (synthetic sensors). Cảm biến gia tốc tuyến tính (linear acceleration sensors) và cảm biến trọng lực là những ví dụ của các cảm biến dựa trên phần mềm.

### Sensor Framework

Framework cảm biến này là 1 phần của package android.hardware bao gồm các classes và interfaces dưới đây

- SensorManager
- Sensor
- SensorEvent
- SensorEventListener

## Identifying Sensors and Sensor Capabilities

- Để xác định các cảm biến trên thiết bị đầu tiên cần dùng sensor service.

```kotlin
private SensorManager sensorManager;
...
sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
```

- Có thể lấy toàn bộ danh sách các cảm biến qua *getSensorList()* với **TYPE_ALL**

```kotlin
List<Sensor> deviceSensors = sensorManager.getSensorList(Sensor.TYPE_ALL);
```

- Ngoài ra có thể lấy danh sách các cảm biến theo loại thay cho TYPE_ALL, TYPE_GYROSCOPE, TYPE_LINEAR_ACCELERATION, TYPE_GRAVITY
- Để kiếm tra xem 1 loại cảm biến có được hỗ trợ trên thiết bị, sử dụng *getDefaultSensor()*

```kotlin
private SensorManager sensorManager;
...
sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
if (sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) != null){
    // Success! There's a magnetometer.
} else {
    // Failure! No magnetometer.
}
```

- Ngoài ra thông qua class **Sensor** có thể biết được các thông số và khả năng của các cảm biến riêng biệt. Sẽ rất tiện lợi trong việc muốn ứng dụng có các hành động khác nhau với các loại sensor khác nhau được hỗ trợ trên thiết bị. Ví dụ có thể sử dụng *getResolution()* để lấy được độ phân giải của cảm biến.
- Trong trường hợp muốn tối ưu hóa ứng dụng với các cảm biến của các nhà sản xuất hoặc phiên bản khác nhau. *getVendor(), getVersion().*

```kotlin
private SensorManager sensorManager;
private Sensor mSensor;

...

sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
mSensor = null;

if (sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY) != null){
    List<Sensor> gravSensors = sensorManager.getSensorList(Sensor.TYPE_GRAVITY);
    for(int i=0; i<gravSensors.size(); i++) {
        if ((gravSensors.get(i).getVendor().contains("Google LLC")) &&
           (gravSensors.get(i).getVersion() == 3)){
            // Use the version 3 gravity sensor.
            mSensor = gravSensors.get(i);
        }
    }
}
if (mSensor == null){
    // Use the accelerometer.
    if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null){
        mSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    } else{
        // Sorry, there are no accelerometers on your device.
        // You can't play this game.
    }
}
```

- 1 function hữu dụng nữa là *getMinDelay()* nó sẽ trả về khoảng thời gian tối thiểu 1 cảm biến có thể cảm nhận dữ liệu. Nếu nó trả về 0 tức cảm biến sẽ chỉ trả về giữ liệu khi có thay đổi.

## Monitoring Sensor Events

- Để theo dõi dữ liệu của cảm biến, cần sử dụng **SensorEventListener: onAccuracyChanged() và onSensorChanged().**
- Độ chính xác: **SENSOR_STATUS_ACCURACY_LOW SENSOR_STATUS_ACCURACY_MEDIUM, SENSOR_STATUS_ACCURACY_HIGH, or SENSOR_STATUS_UNRELIABLE.**
- Giá trị thay đổi:

```kotlin
public class SensorActivity extends Activity implements SensorEventListener {
    private SensorManager sensorManager;
    private Sensor mLight;

    @Override
    public final void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mLight = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
    }

    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do something here if sensor accuracy changes.
    }

    @Override
    public final void onSensorChanged(SensorEvent event) {
        // The light sensor returns a single value.
        // Many sensors return 3 values, one for each axis.
        float lux = event.values[0];
        // Do something with this sensor value.
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, mLight, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }
}
```