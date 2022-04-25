package io.github.mdrscorp.muod.command;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;

import io.github.mdrscorp.muod.Muod;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.TextColor;
import net.minecraft.text.TranslatableText;

public class Data {

    private static final String PATH = "muod\\data";
    private static final int MAX_HOMES = 3;
    public static HashMap<String, PlayerData> DATA = new HashMap<String, PlayerData>();

    public static void register(String uuid, String name) {
        if (!DATA.containsKey(uuid))
            DATA.put(uuid, new PlayerData(name));
    }

    public static boolean doesFileExist() {
        File f = new File(PATH);
        return f.exists();
    }

    public static void load() {
        try {
            File f = new File(PATH);

            FileInputStream fis = new FileInputStream(f);
            ObjectInputStream ois = new ObjectInputStream(fis);

            @SuppressWarnings("unchecked")
            HashMap<String, PlayerData> data = (HashMap<String, PlayerData>) ois.readObject();
            DATA = data;
            ois.close();
            fis.close();
        }
        catch (IOException err) {
            Muod.LOGGER.error(err.toString());
        }
        catch (ClassNotFoundException err) {
            Muod.LOGGER.error(err.toString());
        }
    }

    public static void save() {
        try {
			File f = new File(PATH);
			f.createNewFile();
			
			FileOutputStream fos = new FileOutputStream(f);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			
			oos.writeObject(DATA);
			oos.close();
			fos.close();
			
		} catch (IOException err) {
			Muod.LOGGER.error(err.toString());
		}
    }

    public static class PlayerData implements Serializable {
        public String name;
        public HashMap<String, HomeInfo> homes = new HashMap<String, HomeInfo>();

        public PlayerData(String name) {
            this.name = name;
        }

        public HomeInfo getHome(ServerCommandSource source, String name) {
            if (homes.containsKey(name))
                return homes.get(name);
            source.sendFeedback(new TranslatableText("command.muod.home_error_1")
                .append(new LiteralText(name).fillStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xff0000))))
                .append(new TranslatableText("command.muod.home_error_2")), 
                false);
            return null;
        }

        public void setHome(ServerCommandSource source, String name, int x, int y, int z, String dim) {
            if (homes.containsKey(name))
                source.sendFeedback(new TranslatableText("command.muod.sethome_error_name_taken"), false);
            else if (homes.size() < MAX_HOMES) {
                homes.put(name, new HomeInfo(x, y, z, dim));
                source.sendFeedback(new TranslatableText("command.muod.sethome_success_1")
                    .append(new LiteralText(name).fillStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x00ff00))))
                    .append(new TranslatableText("command.muod.sethome_success_2")), 
                    false);
            }
            else
                source.sendFeedback(new TranslatableText("command.muod.sethome_error_max_home_number_1")
                    .append(new LiteralText("" + MAX_HOMES).fillStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xff0000))))
                    .append(new TranslatableText("command.muod.sethome_error_max_home_number_2")), 
                    false);
        }

        public void delHome(ServerCommandSource source, String name) {
            if (homes.containsKey(name)) {
                homes.remove(name);
                source.sendFeedback(new TranslatableText("command.muod.delhome_success_1")
                    .append(new LiteralText(name).fillStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x00ff00))))
                    .append(new TranslatableText("command.muod.delhome_success_2")), 
                    false);
            }
            else
                source.sendFeedback(new TranslatableText("command.muod.delhome_error")
                    .append(new LiteralText(name).fillStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xff0000)))), 
                    false);
        }
    }

    public static class HomeInfo implements Serializable {
        
        public int x, y, z;
        public String dim;

        public HomeInfo(int x, int y, int z, String dim) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.dim = dim;
        }
    }
}
