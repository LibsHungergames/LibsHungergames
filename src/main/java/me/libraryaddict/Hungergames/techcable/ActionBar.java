/*
* The MIT License
* Copyright (c) 2015 Techcable
*
* Permission is hereby granted, free of charge, to any person obtaining a copy
* of this software and associated documentation files (the "Software"), to deal
* in the Software without restriction, including without limitation the rights
* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions:
*
* The above copyright notice and this permission notice shall be included in
* all copies or substantial portions of the Software.
*
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
* THE SOFTWARE.
*/

package me.libraryaddict.Hungergames.techcable;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import lombok.Getter;
import org.bukkit.entity.Player;

import static me.libraryaddict.Hungergames.techcable.Reflection.*; //Make sure to change this to the right package

/**
 * A 1.8 ActionBar
 *
 * Works on protocol hack and real 1.8
 */
public class ActionBar {
    /**
     * Creates a new action bar with the specificed text
     *
     * @param text text to keep in the action bar
     */
    public ActionBar(String text) {
        this.text = text;
    }
    @Getter
    private final String text;

    public void sendTo(Player p) {
        ActionBarHandler handler = getActionBarHandler();
        if (handler == null) return;
        handler.sendTo(p, this);
    }

    public static boolean isSupported() {
        return getActionBarHandler() != null;
    }

    private static ActionBarHandler handler;
    private static ActionBarHandler getActionBarHandler() {
        if (handler != null) return handler;
        if (SpigotActionBarHandler.isSupported()) {
            handler = new SpigotActionBarHandler();
        } else if (NMSActionBarHandler.isSupported()) {
            handler = new NMSActionBarHandler();
        } else {
            return null;
        }
        return handler;
    }

    private static interface ActionBarHandler {
        public void sendTo(Player p, ActionBar bar);
    }

    private static class SpigotActionBarHandler implements ActionBarHandler {
        private SpigotActionBarHandler() {
            assert isSupported() : "Spigot action bar is unsupported!";
        }

        private final static Constructor packetConstructor = makeConstructor(getNmsClass("PacketPlayOutChat"), getNmsClass("IChatBaseComponent"), int.class);
        public void sendTo(Player p, ActionBar bar) {
            if (getProtocolVersion(p) < 16) return;
            Object baseComponent = serialize(bar.getText());
            Object packet = callConstructor(packetConstructor, baseComponent, 2);
            sendPacket(p, packet);
        }

        private static final Field playerConnectionField = makeField(getNmsClass("EntityPlayer"), "playerConnection");
        private static final Field networkManagerField = makeField(playerConnectionField.getClass(), "networkManager");
        private static final Method getVersion = makeMethod(networkManagerField.getClass(), "getVersion");
        private static int getProtocolVersion(Player player) {
            Object handle = getHandle(player);
            Object connection = getField(playerConnectionField, handle);
            Object networkManager = getField(networkManagerField, connection);
            assert getVersion() != null : "Not Protocol Hack";
            int version = callMethod(getVersion, networkManager);
            return version;
        }

        public static boolean isSupported() {
            return Reflection.getClass("org.spigotmc.ProtocolData") != null;
        }
    }

    private static class NMSActionBarHandler implements ActionBarHandler {

        private NMSActionBarHandler() {
            assert !SpigotActionBarHandler.isSupported() : "Spigot action bar is supported";
            assert NMSActionBarHandler.isSupported(): "NMS Action bar isn't supported";
        }

        private static final Constructor packetConstructor = makeConstructor(getNmsClass("PacketPlayOutChat"), getNmsClass("IChatBaseComponent"), int.class);
        public void sendTo(Player p, ActionBar bar) {
            Object baseComponent = serialize(bar.getText());
            Object packet = callConstructor(packetConstructor, baseComponent, 2);
            sendPacket(p, packet);
        }

        public static boolean isSupported() {
            return packetConstructor != null;
        }
    }

    //Utils

    private static final Field playerConnectionField = makeField(getNmsClass("EntityPlayer"), "playerConnection");
    private static final Method sendPacketMethod = makeMethod(getNmsClass("PlayerConnection"), "sendPacket", getNmsClass("Packet"));
    private static void sendPacket(Player player, Object packet) {
        Object handle = getHandle(player);
        Object connection = getField(playerConnectionField, handle);
        callMethod(sendPacketMethod, connection, packet);
    }

    private static final Method addSiblingMethod = makeMethod(getNmsClass("IChatBaseComponent"), "addSibling", getNmsClass("IChatBaseComponent"));
    private static final Method fromStringMethod = makeMethod(getCbClass("util.CraftChatMessage"), "fromString", String.class);
    private static Object serialize(String text) { //Serialize to IChatBaseComponent
        Object baseComponentArray = callMethod(fromStringMethod, null, text);;
        Object first = null;
        for (int i = 0; i < Array.getLength(baseComponentArray); i++) {
            Object baseComponent = Array.get(baseComponentArray, i);
            if (first == null) {
                first = baseComponent;
            } else {
                first = callMethod(addSiblingMethod, first, baseComponent);
            }
        }
        return first;
    }
}
