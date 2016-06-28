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

import lombok.*;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import javax.annotation.Nullable;

import com.google.common.base.Preconditions;
import com.google.common.base.Verify;

import org.bukkit.entity.Player;

import static com.google.common.base.Preconditions.*;
import static me.libraryaddict.Hungergames.techcable.Reflection.*;

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
        return addSiblingMethod != null && fromStringMethod != null && playerConnectionField != null && sendPacketMethod != null && SpigotActionBarHandler.isSupported() || NMSActionBarHandler.isSupported();
    }

    private static ActionBarHandler handler;
    private static ActionBarHandler getActionBarHandler() {
        if (handler != null) return handler;
        checkState(isSupported(), "Not supported!");
        if (SpigotActionBarHandler.isSupported()) {
            handler = new SpigotActionBarHandler();
        } else if (NMSActionBarHandler.isSupported()) {
            handler = new NMSActionBarHandler();
        } else {
            return null;
        }
        return handler;
    }

    private interface ActionBarHandler {
        void sendTo(Player p, ActionBar bar);
    }

    @Nullable
    private static final Class<?> PACKET_CLASS = getNmsClass("Packet");
    @Nullable
    private static final Class<?> CHAT_PACKET_CLASS = getNmsClass("PacketPlayOutChat");
    @Nullable
    private static final Class<?> CHAT_COMPONENT_CLASS = getNmsClass("IChatBaseComponent");
    @Nullable
    private static final Class<?> ENTITY_PLAYER_CLASS = getNmsClass("PlayerConnection");
    @Nullable
    private static final Class<?> NETWORK_MANAGER_CLASS = getNmsClass("NetworkManager");
    @Nullable
    private static final Class<?> PLAYER_CONNECTION_CLASS = getNmsClass("PlayerConnection");
    @Nullable
    private static final Class<?> CRAFT_CHAT_MESSAGE_CLASS = getCbClass("util.CraftChatMessage");

    private static class SpigotActionBarHandler implements ActionBarHandler {
        private SpigotActionBarHandler() {
            assert isSupported() : "Spigot action bar is unsupported!";
        }

        private final static Constructor packetConstructor = CHAT_PACKET_CLASS != null && CHAT_COMPONENT_CLASS != null ? makeConstructor(CHAT_PACKET_CLASS, CHAT_COMPONENT_CLASS, int.class) : null;
        public void sendTo(Player p, ActionBar bar) {
            if (getProtocolVersion(p) < 16) return;
            Object baseComponent = serialize(bar.getText());
            Object packet = callConstructor(packetConstructor, baseComponent, 2);
            sendPacket(p, packet);
        }

        private static final Field networkManagerField = PLAYER_CONNECTION_CLASS != null ? makeField(PLAYER_CONNECTION_CLASS, "networkManager") : null;
        private static final Method getVersionMethod = NETWORK_MANAGER_CLASS != null ? makeMethod(NETWORK_MANAGER_CLASS, "getVersion") : null;
        private static int getProtocolVersion(Player player) {
            Object handle = getHandle(player);
            Object connection = getField(playerConnectionField, handle);
            Object networkManager = getField(networkManagerField, connection);
            return callMethod(getVersionMethod, networkManager);
        }

        public static boolean isSupported() {
            return Reflection.getClass("org.spigotmc.ProtocolData") != null && networkManagerField != null && getVersionMethod != null;
        }
    }

    private static class NMSActionBarHandler implements ActionBarHandler {

        private NMSActionBarHandler() {
            assert !SpigotActionBarHandler.isSupported() : "Spigot action bar is supported";
            assert NMSActionBarHandler.isSupported(): "NMS Action bar isn't supported";
        }

        private static final Constructor packetConstructor = CHAT_PACKET_CLASS != null && CHAT_COMPONENT_CLASS != null ? makeConstructor(CHAT_PACKET_CLASS, CHAT_COMPONENT_CLASS, int.class) : null;
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

    private static final Field playerConnectionField = ENTITY_PLAYER_CLASS != null ? makeField(ENTITY_PLAYER_CLASS, "playerConnection") : null;
    private static final Method sendPacketMethod = PLAYER_CONNECTION_CLASS != null && PACKET_CLASS != null ? makeMethod(PLAYER_CONNECTION_CLASS, "sendPacket", PACKET_CLASS) : null;
    private static void sendPacket(Player player, Object packet) {
        Object handle = getHandle(player);
        Object connection = getField(playerConnectionField, handle);
        callMethod(sendPacketMethod, connection, packet);
    }

    private static final Method addSiblingMethod = CHAT_COMPONENT_CLASS != null ? makeMethod(CHAT_COMPONENT_CLASS, "addSibling", CHAT_COMPONENT_CLASS) : null;
    private static final Method fromStringMethod = CRAFT_CHAT_MESSAGE_CLASS != null ? makeMethod(CRAFT_CHAT_MESSAGE_CLASS, "fromString", String.class) : null;
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
