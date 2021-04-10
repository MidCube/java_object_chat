package uk.ac.cam.cig23.fjava.tick2;

import uk.ac.cam.cl.fjava.messages.*;

import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@FurtherJavaPreamble(author="Cameron Griffiths", date="23/10/2020", crsid="cig23", summary="This is my chat client", ticker= FurtherJavaPreamble.Ticker.C)
public class ChatClient {

    public static void main(String[] args) {
        String server = null;
        int port = 0;

        if(args.length != 2){
            System.err.println("This application requires two arguments: <machine> <port>");
            return;
        } else {
            try {
                server = args[0];
                port = Integer.parseInt(args[1]);
            } catch (NumberFormatException ex) {
                System.err.println("This application requires two arguments: <machine> <port>");
                return;
            }
        }

        final Socket s;
        try {
            s = new Socket(server, port);
            DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
            System.out.println(dateFormat.format(new Date())+" [Client] Connected to "+server+" on port "+port+".");
        } catch (java.io.IOException ex) {
            System.err.format("Cannot connect to %s on port %s\n", server, port);
            return;
        }
        Thread output =
                new Thread() {
                    @Override
                    public void run() {
                        try {
                            InputStream input = s.getInputStream();
                            DynamicObjectInputStream inputStream = new DynamicObjectInputStream(input);
                            while(true) {

                                Object message = inputStream.readObject();
                                if (message instanceof RelayMessage) {
                                    RelayMessage myMessage = (RelayMessage) message;
                                    //print time [name] Message
                                    DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
                                    System.out.println(dateFormat.format(myMessage.getCreationTime()) + " [" + myMessage.getFrom() + "] " + myMessage.getMessage());
                                } else if (message instanceof StatusMessage) {
                                    StatusMessage myMessage = (StatusMessage) message;
                                    //print time [Server] Message
                                    DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
                                    System.out.println(dateFormat.format(myMessage.getCreationTime()) + " [Server] " + myMessage.getMessage());
                                } else if (message instanceof NewMessageType) {
                                    NewMessageType newMessage = (NewMessageType) message;
                                    DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");


                                    System.out.println(dateFormat.format(newMessage.getCreationTime()) + " [Client] New class "+newMessage.getName()+" loaded." );
                                    inputStream.addClass(newMessage.getName(),newMessage.getClassData());
                                } else {
                                    DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
                                    Class<?> newClass = message.getClass();
                                    Field[] declaredFields = newClass.getDeclaredFields();
                                    Method[] methods = newClass.getDeclaredMethods();
                                    List<Method> runMe = new ArrayList<Method>();
                                    for (Method method : methods) {
                                        Class[] parameterTypes = method.getParameterTypes();
                                        Annotation[] annotations = method.getAnnotations();
                                        if(parameterTypes.length == 0) {
                                            for(Annotation annotation : annotations) {
                                                if (annotation instanceof Execute) {
                                                    runMe.add(method);
                                                }
                                            }
                                        }
                                    }
                                    String created = ": ";
                                    Object myMessage = message;
                                    boolean flag = false;
                                    for (Field item : declaredFields) {
                                        item.setAccessible(true);
                                        flag=true;
                                        try {
                                            created += item.getName() + "(" + item.get(myMessage) + ")" + ", ";
                                        } catch (IllegalAccessException ex) {
                                            System.exit(0);
                                        }
                                    }
                                    if(flag) {
                                        created=created.substring(0, created.length() - 2);
                                    }
                                    System.out.println(dateFormat.format(new Date()) + " [Client] "+newClass.getSimpleName()+""+created );
                                    for(Method method : runMe) {
                                        try {
                                            method.invoke(myMessage);
                                        } catch (IllegalAccessException | InvocationTargetException ex) {
                                            System.exit(0);
                                        }
                                    }
                                }
                            }
                        } catch (ClassNotFoundException ex) {
                            DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
                            System.out.println(dateFormat.format(new Date()) + " [Client] New message of unknown type received." );
                        } catch (IOException ex) {
                            System.err.println(ex);
                            return;
                        }

                    }
                };
        output.setDaemon(true);
        output.start();
        BufferedReader r = new BufferedReader(new InputStreamReader(System.in));
        try {
            OutputStream input = s.getOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(input);
            while (true) {

                    String str = r.readLine();
                    //if str starts with \ do command stuff
                if(str.startsWith("\\")) {
                    if(str.startsWith("\\nick")) {
                        //remove nick message and send a ChangeNickMessage to server
                        str = str.replace("\\nick ", "");
                        ChangeNickMessage newMessage = new ChangeNickMessage(str);
                        out.writeObject(newMessage);
                    } else if (str.startsWith("\\quit")) {
                        //print quitting message and quit
                        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
                        System.out.format("%s [Client] Connection terminated.\n", dateFormat.format(new Date()));
                        System.exit(0);
                    } else {
                        //display command not recognised
                        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
                        System.out.println(dateFormat.format(new Date()) +" [Client] Unknown command \""+str.replace("\\","")+"\"");
                    }
                } else {
                    //else send normal message
                    ChatMessage myMessage = new ChatMessage(str);
                    out.writeObject(myMessage);
                }
            }
        } catch (IOException ex) {
            System.err.println(ex);
            return;
        }
    }
}