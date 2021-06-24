package cn.rt.client;

import cn.rt.client.command.Command;
import cn.rt.client.command.LoginCommand;
import cn.rt.client.common.CommandType;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * @author ruanting
 * @date 2021/6/22
 */
public class RimClient {
    public static final String ROUTE_ADDR = "localhost:8081/route";

    private static Map<String, Command> commandMap;

    static {
        commandMap = new HashMap<>();
        commandMap.put(CommandType.login.getValue(), new LoginCommand());
    }

    public static void main(String[] args) {
        startConsoleThread();
    }

    private static void startConsoleThread() {
        Scanner sc = new Scanner(System.in);
        boolean flag = true;

        while (flag) {
            String command = sc.nextLine();
            doCommand(command);
        }
    }

    private static void doCommand(String command) {
        if (StringUtils.isEmpty(command)) {
            return;
        }
        Command c = commandMap.get(command);
        if (c == null) {
            System.out.println("不存在此命令");
            return;
        }
        c.doCommand();
    }
}
