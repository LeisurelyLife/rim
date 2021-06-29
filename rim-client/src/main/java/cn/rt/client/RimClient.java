package cn.rt.client;

import cn.rt.client.command.Command;
import cn.rt.client.command.LoginCommand;
import cn.rt.client.common.CommandType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * @author ruanting
 * @date 2021/6/22
 */
@Slf4j
public class RimClient {
    public static final String ROUTE_ADDR = "localhost:8081/route";

    private static Map<String, Class> commandMap;

    public static String userName = "";

    public static String cacheName = "";

    public static boolean isLogin = false;

    static {
        commandMap = new HashMap<>();
        commandMap.put(CommandType.login.getValue(), LoginCommand.class);
    }

    public static void main(String[] args) {
        startConsoleThread();
    }

    private static void startConsoleThread() {
        Scanner sc = new Scanner(System.in);
        boolean flag = true;
        System.out.println("欢迎！");
        while (flag) {
            System.out.print(userName + ":");
            String command = sc.nextLine();
            doCommand(command);
        }
    }

    private static void doCommand(String command) {
        if (StringUtils.isEmpty(command)) {
            return;
        }
        if (CommandType.help.getValue().equals(command)) {
            printCommands();
            return;
        }
        Command c = getCommand(command);
        if (c == null) {
            System.out.println("不存在此命令");
            return;
        }
        c.doCommand();
    }

    private static void printCommands() {
        for (CommandType value : CommandType.values()) {
            System.out.println(value.getValue() + ":" + value.getDescription());
        }
    }

    private static Command getCommand(String command) {
        Class aClass = commandMap.get(command);
        if (aClass == null) {
            return null;
        }
        Command c = null;
        try {
            c = (Command) aClass.newInstance();
        } catch (InstantiationException e) {
            log.error("获取命令失败：", e);
        } catch (IllegalAccessException e) {
            log.error("获取命令失败：", e);
        }
        return c;
    }
}
