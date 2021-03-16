package com.conaxgames.practice.util.adapter;

import com.google.gson.*;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class ItemStackTypeAdapter
        implements JsonDeserializer<ItemStack>, JsonSerializer<ItemStack> {

    public ItemStack deserialize(JsonElement object, Type type, JsonDeserializationContext context)
            throws JsonParseException {
        if (!(object instanceof JsonObject)) {
            return new ItemStack(Material.AIR);
        }

        JsonObject element = (JsonObject) object;

        int id = element.get("id").getAsInt();
        short data = element.has("damage")
                ? element.get("damage").getAsShort()
                : (element.has("data")
                ? element.get("data").getAsShort()
                : 0);
        int count = element.get("count").getAsInt();

        ItemStack item = new ItemStack(id, count, data);
        ItemMeta meta = item.getItemMeta();

        if (element.has("name")) {
            meta.setDisplayName(element.get("name").getAsString());
        }

        if (element.has("lore")) {
            meta.setLore(convertStringList(element.get("lore")));
        }

        if (element.has("color")) {
            ((LeatherArmorMeta) meta).setColor(Color.fromRGB(element.get("color").getAsInt()));
        } else if (element.has("skull")) {
            ((SkullMeta) meta).setOwner(element.get("skull").getAsString());
        } else if (element.has("title")) {
            ((BookMeta) meta).setTitle(element.get("title").getAsString());
            ((BookMeta) meta).setAuthor(element.get("author").getAsString());
            ((BookMeta) meta).setPages(convertStringList(element.get("pages")));
        } else if (element.has("stored-enchants")) {
            JsonObject enchantments = (JsonObject) element.get("stored-enchants");

            for (Enchantment enchantment : Enchantment.values()) {
                if (enchantments.has(enchantment.getName())) {
                    ((EnchantmentStorageMeta) meta).addStoredEnchant(enchantment, enchantments.get(enchantment.getName()).getAsInt(), true);
                }
            }
        }

        item.setItemMeta(meta);

        if (element.has("enchants")) {
            JsonObject enchantments = (JsonObject) element.get("enchants");
            for (Enchantment enchantment : Enchantment.values()) {
                if (enchantments.has(enchantment.getName())) {
                    item.addUnsafeEnchantment(enchantment, enchantments.get(enchantment.getName()).getAsInt());
                }
            }
        }

        return item;
    }

    public JsonElement serialize(ItemStack item, Type type, JsonSerializationContext context) {
        if (item == null) {
            item = new ItemStack(Material.AIR);
        }

        JsonObject element = new JsonObject();

        element.addProperty("id", item.getTypeId());
        element.addProperty(getDataKey(item), item.getDurability());
        element.addProperty("count", item.getAmount());

        if (item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();

            if (meta.hasDisplayName()) {
                element.addProperty("name", meta.getDisplayName());
            }

            if (meta.hasLore()) {
                element.add("lore", convertStringList(meta.getLore()));
            }

            if (meta instanceof LeatherArmorMeta) {
                element.addProperty("color", ((LeatherArmorMeta) meta).getColor().asRGB());
            } else if (meta instanceof SkullMeta) {
                element.addProperty("skull", ((SkullMeta) meta).getOwner());
            } else if (meta instanceof BookMeta) {
                element.addProperty("title", ((BookMeta) meta).getTitle());
                element.addProperty("author", ((BookMeta) meta).getAuthor());
                element.add("pages", convertStringList(((BookMeta) meta).getPages()));
            } else if (meta instanceof EnchantmentStorageMeta) {
                JsonObject storedEnchantments = new JsonObject();

                for (Map.Entry<Enchantment, Integer> entry :
                        ((EnchantmentStorageMeta) meta).getStoredEnchants().entrySet()) {
                    storedEnchantments.addProperty(entry.getKey().getName(), entry.getValue());
                }

                element.add("stored-enchants", storedEnchantments);
            }
        }

        if (item.getEnchantments().size() != 0) {
            JsonObject enchantments = new JsonObject();

            for (Map.Entry<Enchantment, Integer> entry : item.getEnchantments().entrySet()) {
                enchantments.addProperty(entry.getKey().getName(), entry.getValue());
            }

            element.add("enchants", enchantments);
        }

        return element;
    }

    private String getDataKey(ItemStack item) {
        if (item.getType() == Material.AIR) {
            return "data";
        }

        if (Enchantment.DURABILITY.canEnchantItem(item)) {
            return "damage";
        }

        return "data";
    }

    private JsonArray convertStringList(Collection<String> strings) {
        JsonArray ret = new JsonArray();
        for (String string : strings) {
            ret.add(new JsonPrimitive(string));
        }
        return ret;
    }

    private List<String> convertStringList(JsonElement jsonElement) {
        JsonArray array = jsonElement.getAsJsonArray();
        List<String> ret = new ArrayList<>();

        for (JsonElement element : array) {
            ret.add(element.getAsString());
        }

        return ret;
    }
}
