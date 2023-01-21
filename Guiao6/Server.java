import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.locks.ReentrantLock;

public class Server {

    public static void main(String[] args) throws IOException {
        ServerSocket ss = new ServerSocket(12345);
        Data data = new Data();
        while (true) {
            Socket socket = ss.accept();
            Thread s = new Thread(new ServerWorker(socket,data));
            s.start();
        }
    }

    private static class Data {
        private int sum_total = 0;
        private int num_total = 0;
        private final ReentrantLock l = new ReentrantLock();
        private void add_sum(int n) {
            try {
                l.lock();
                sum_total += n;
                num_total++;
            } finally {
                l.unlock();
            }
        }
        private double check_average() {
            try {
                l.lock();
                if (num_total == 0) return 0;
                return (double) sum_total/num_total;
            }
            finally {
                l.unlock();
            }
        }
    }

    private static class ServerWorker implements Runnable {

        private Socket socket;
        private Data data;

        ServerWorker(Socket socket, Data data) {
            this.socket = socket;
            this.data = data;
        }

        public void run() {
            try {

                while (true) {

                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    PrintWriter out = new PrintWriter(socket.getOutputStream());

                    int sum = 0;

                    String line;
                    while ((line = in.readLine()) != null) {
                        try {
                            int n = Integer.parseInt(line);
                            sum += n;
                            data.add_sum(n);
                        }
                        catch (NumberFormatException e){
                            e.printStackTrace();
                        }
                        out.println("Server response: " + sum);
                        out.flush();
                    }

                    socket.shutdownInput();
                    out.println("Server mean: " + data.check_average());
                    out.flush();
                    socket.shutdownOutput();
                    socket.close();
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }

    }




}
