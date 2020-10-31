package com.github.eokasta.spawners.utils;

import com.google.common.io.BaseEncoding;
import org.bukkit.inventory.ItemStack;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;

public class ItemSerializer {

    public static String serializeItem(ItemStack itemStack) {
        if (itemStack == null) {
            return null;
        }
        ByteArrayOutputStream outputStream = null;
        try {
            Class<?> nbtTagCompoundClass = Reflection.getNMSClass("NBTTagCompound");
            Constructor<?> nbtTagCompoundConstructor = nbtTagCompoundClass.getConstructor();
            Object nbtTagCompound = nbtTagCompoundConstructor.newInstance();
            Object nmsItemStack = Reflection.getBukkitClass("inventory.CraftItemStack")
                    .getMethod("asNMSCopy", ItemStack.class).invoke(null, itemStack);
            Reflection.getNMSClass("ItemStack").getMethod("save", nbtTagCompoundClass).invoke(nmsItemStack, nbtTagCompound);
            outputStream = new ByteArrayOutputStream();
            Reflection.getNMSClass("NBTCompressedStreamTools").getMethod("a", nbtTagCompoundClass, OutputStream.class).invoke(null,
                    nbtTagCompound, outputStream);
        } catch (Exception e) {
        }
        return BaseEncoding.base64().encode(outputStream.toByteArray());
    }

    public static ItemStack deserializeItem(String itemStackString) {
        ItemStack itemStack = null;
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(BaseEncoding.base64().decode(itemStackString));
            Class<?> nbtTagCompoundClass = Reflection.getNMSClass("NBTTagCompound");
            Class<?> nmsItemStackClass = Reflection.getNMSClass("ItemStack");
            Object nbtTagCompound = null;
            nbtTagCompound = Reflection.getNMSClass("NBTCompressedStreamTools").getMethod("a", InputStream.class).invoke(null,
                    inputStream);
            Object craftItemStack = nmsItemStackClass.getMethod("createStack", nbtTagCompoundClass).invoke(null,
                    nbtTagCompound);
            itemStack = (ItemStack) Reflection.getBukkitClass("inventory.CraftItemStack")
                    .getMethod("asBukkitCopy", nmsItemStackClass).invoke(null, craftItemStack);
        } catch (Exception e) {
        }
        return itemStack;
    }

}
