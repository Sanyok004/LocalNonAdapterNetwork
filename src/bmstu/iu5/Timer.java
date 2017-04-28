package bmstu.iu5;

public class Timer implements Runnable{

    Timer(){
        new Thread(this).start();
    }

    @Override
    public void run() {
        for(; Main.time > 0; Main.time--){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Time is out!!!");
        Main.chat.connectionFailed();
    }
}
