数字音频合成  digital audio synthesis 

声音是由重复的气压变化形成， 以波的形式存在，某些特定频率的振荡是可听见的，
范围是  12Hz ~ 20kHz
12Hz (每秒12个周期，这是一种很低的声音，如隆隆声)   
20kHz（每秒20000个周期，这是一种非常高的声音）


为创建音频， 需要使空气以期望的声音频率振荡，
在数字领域，通常使用由模拟电信号驱动的扬声器来实现，
数字音频系统包含一个芯片和电路板， 来执行“数字～模拟”转换（digital-to-analog conversion，DAC）

为了合成音频， 只需合成音频样本，并将它们提交给适当的机制， AudioTrack类就是这种机制



8.1.1  播放合成声音

此示例使用一个内部类，  并扩展 AsyncTask
AsyncTask 定义了一个 doInBackground方法，它在一个单独的线程中运行其中的代码， 该线程与活动的主线程分离
这样使得活动以及UI可以保持响应，否则向AudioTrack对象的write方法提供数据的循环会使主线程阻塞


//音频样本，每个数字代表波形上的一个点
            // 样本从8130 振荡到32695， 向下振荡到 -32121， 然后回到 -466
//            short[] buffer = {8130, 15752, 22389, 27625, 31134, 32695, 32210, 29711, 25354, 19410, 12253,
//            4329, -3865, -11818, -19032, -25055, -29511, -32121, -32722, -31276, -27874, -22728, -16160, -8582, -466};

            //这是一个短波形，十个样本，表示一个高频声音，每秒存在许多振荡
            //而低频声音的波形将会以一个固定的采样率覆盖更多的样本
            short[] buffer = {8130,15752,32695,12253,4329,-3865,-19032,-32722,16160,-466};
