package io.github.stuff_stuffs.tbcexcore.client.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.stuff_stuffs.tbcexcore.client.util.ItemStackInfo;
import io.github.stuff_stuffs.tbcexcore.common.battle.Battle;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantHandle;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.BattleParticipantStateView;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.inventory.BattleParticipantInventoryHandle;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.inventory.BattleParticipantItemCategory;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.inventory.BattleParticipantItemStack;
import io.github.stuff_stuffs.tbcexcore.common.battle.participant.inventory.equipment.BattleEquipmentSlot;
import io.github.stuff_stuffs.tbcexcore.mixin.api.BattleWorldSupplier;
import io.github.stuff_stuffs.tbcexgui.client.widget.AbstractWidget;
import io.github.stuff_stuffs.tbcexutil.client.RenderUtil;
import io.github.stuff_stuffs.tbcexutil.common.Rect2d;
import io.github.stuff_stuffs.tbcexutil.common.colour.Colour;
import io.github.stuff_stuffs.tbcexutil.common.colour.IntRgbColour;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.world.World;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.DoubleSupplier;
import java.util.function.IntConsumer;

public class BattleInventoryFilterWidget extends AbstractWidget {
    public static final Colour FIRST_BACKGROUND_COLOUR = new IntRgbColour(0x00111111);
    public static final Colour SECOND_BACKGROUND_COLOUR = new IntRgbColour(0x00222222);

    public static final List<Category> DEFAULTS = Util.make(new ArrayList<>(), list -> {
        list.add(new Category() {
            @Override
            public List<ItemStackInfo> filter(final List<ItemStackInfo> infos) {
                return infos;
            }

            @Override
            public Text getName() {
                return new LiteralText("ALL");
            }
        });
        list.add(new Category() {
            @Override
            public List<ItemStackInfo> filter(final List<ItemStackInfo> infos) {
                return infos.stream().filter(item -> item.stack.getItem().isInCategory(BattleParticipantItemCategory.CONSUMABLE_CATEGORY)).toList();
            }

            @Override
            public Text getName() {
                return new LiteralText("CONSUMABLES");
            }
        });
        list.add(new Category() {
            @Override
            public List<ItemStackInfo> filter(final List<ItemStackInfo> infos) {
                return infos.stream().filter(item -> item.stack.getItem().isInCategory(BattleParticipantItemCategory.INVALID_CATEGORY)).toList();
            }

            @Override
            public Text getName() {
                return new LiteralText("INVALID");
            }
        });
        list.add(new Category() {
            @Override
            public List<ItemStackInfo> filter(final List<ItemStackInfo> infos) {
                return infos.stream().filter(item -> {
                    for (final BattleEquipmentSlot slot : BattleEquipmentSlot.REGISTRY) {
                        if (item.stack.getItem().isInCategory(BattleParticipantItemCategory.BATTLE_EQUIPMENT_CATEGORY.apply(slot))) {
                            return true;
                        }
                    }
                    return false;
                }).toList();
            }

            @Override
            public Text getName() {
                return new LiteralText("EQUIPMENT");
            }
        });
        for (BattleEquipmentSlot slot : BattleEquipmentSlot.REGISTRY) {
            list.add(new Category() {
                @Override
                public List<ItemStackInfo> filter(final List<ItemStackInfo> infos) {
                    return infos.stream().filter(item -> item.stack.getItem().isInCategory(BattleParticipantItemCategory.BATTLE_EQUIPMENT_CATEGORY.apply(slot))).toList();
                }

                @Override
                public Text getName() {
                    return new LiteralText("EQUIPMENT(").append(slot.name()).append(")");
                }
            });
        }
    });
    private final WidgetPosition position;
    private final DoubleSupplier width;
    private final DoubleSupplier height;
    private final double borderThickness;
    private final double entryHeight;
    private final double verticalSpacing;
    private final World world;
    private final BattleParticipantHandle handle;
    private final List<Category> categories;
    private final IntConsumer onSelect;
    private double pos = 0;
    private int selectedIndex = 0;

    public BattleInventoryFilterWidget(final WidgetPosition position, final DoubleSupplier width, final DoubleSupplier height, final double borderThickness, final double entryHeight, final double verticalSpacing, final World world, final BattleParticipantHandle handle, final List<Category> categories, final IntConsumer onSelect) {
        this.position = position;
        this.width = width;
        this.height = height;
        this.borderThickness = borderThickness;
        this.entryHeight = entryHeight;
        this.verticalSpacing = verticalSpacing;
        this.world = world;
        this.handle = handle;
        this.categories = categories;
        this.onSelect = onSelect;
    }

    @Override
    public WidgetPosition getWidgetPosition() {
        return position;
    }

    public List<ItemStackInfo> getFiltered() {
        final Battle battle = ((BattleWorldSupplier) world).tbcex_getBattleWorld().getBattle(handle.battleId());
        if (battle != null) {
            final BattleParticipantStateView participant = battle.getState().getParticipant(handle);
            if (participant != null) {
                final List<ItemStackInfo> infos = new ArrayList<>();
                final Iterator<BattleParticipantInventoryHandle> iterator = participant.getInventoryIterator();
                while (iterator.hasNext()) {
                    final BattleParticipantInventoryHandle next = iterator.next();
                    infos.add(new ItemStackInfo(next, participant.getItemStack(next)));
                }
                for (final BattleEquipmentSlot slot : BattleEquipmentSlot.REGISTRY) {
                    final BattleParticipantItemStack stack = participant.getEquipmentStack(slot);
                    if (stack != null) {
                        infos.add(new ItemStackInfo(handle, slot, stack));
                    }
                }
                if (selectedIndex >= 0 && selectedIndex < categories.size()) {
                    return categories.get(selectedIndex).filter(infos);
                }
            }
        }
        return new ArrayList<>();
    }

    public void setSelectedIndex(final int selectedIndex) {
        if (0 <= selectedIndex && selectedIndex < categories.size()) {
            if (selectedIndex != this.selectedIndex) {
                pos = (position.getY() + borderThickness + selectedIndex * entryHeight + selectedIndex * verticalSpacing + entryHeight / 2) - (height.getAsDouble() - 2 * borderThickness) / 2;
                this.selectedIndex = selectedIndex;
                onSelect.accept(selectedIndex);
            }
        } else {
            this.selectedIndex = -1;
            onSelect.accept(selectedIndex);
        }
    }

    @Override
    public boolean mouseClicked(final double mouseX, final double mouseY, final int button) {
        if (new Rect2d(position.getX(), position.getY(), position.getX() + width.getAsDouble(), position.getY() + height.getAsDouble()).isIn(mouseX, mouseY)) {
            final int index = findHoverIndex(mouseX, mouseY + pos);
            setSelectedIndex(index);
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseReleased(final double mouseX, final double mouseY, final int button) {
        return new Rect2d(position.getX(), position.getY(), position.getX() + width.getAsDouble(), position.getY() + height.getAsDouble()).isIn(mouseX, mouseY);
    }

    @Override
    public boolean mouseDragged(final double mouseX, final double mouseY, final int button, final double deltaX, final double deltaY) {
        final double height = this.height.getAsDouble();
        if (new Rect2d(position.getX(), position.getY(), position.getX() + width.getAsDouble(), position.getY() + height).isIn(mouseX, mouseY)) {
            pos = Math.min(Math.max(pos - deltaY, -(height - 2 * borderThickness) / 2), getListHeight() - (height - 2 * borderThickness) / 2);
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(final double mouseX, final double mouseY, final double amount) {
        final double height = this.height.getAsDouble();
        if (new Rect2d(position.getX(), position.getY(), position.getX() + width.getAsDouble(), position.getY() + height).isIn(mouseX, mouseY)) {
            pos = Math.min(Math.max(pos - amount, -(height - 2 * borderThickness) / 2), getListHeight() - (height - 2 * borderThickness) / 2);
            return true;
        }
        return false;
    }

    @Override
    public void render(final MatrixStack matrices, final double mouseX, final double mouseY, final float delta) {
        final Matrix4f model = matrices.peek().getPositionMatrix();
        final BufferBuilder buffer = Tessellator.getInstance().getBuffer();
        RenderSystem.enableBlend();
        RenderSystem.disableTexture();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        final double offsetX = position.getX();
        final double offsetY = position.getY();
        final double width = this.width.getAsDouble();
        final double height = this.height.getAsDouble();
        buffer.vertex(model, (float) (offsetX + width), (float) offsetY, 0).color(0, 0, 0, 127).next();
        buffer.vertex(model, (float) offsetX, (float) offsetY, 0).color(0, 0, 0, 127).next();
        buffer.vertex(model, (float) offsetX, (float) (offsetY + height), 0).color(0, 0, 0, 127).next();
        buffer.vertex(model, (float) (offsetX + width), (float) (offsetY + height), 0).color(0, 0, 0, 127).next();
        buffer.end();
        BufferRenderer.draw(buffer);

        matrices.push();
        matrices.translate(0, -pos, 0);

        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        final int hoverIndex = findHoverIndex(mouseX, mouseY + pos);
        for (int i = 0; i < categories.size(); i++) {
            renderInfo(categories.get(i), buffer, matrices, i, hoverIndex);
        }
        buffer.end();
        BufferRenderer.draw(buffer);

        render(vertexConsumers -> {
            for (int i = 0; i < categories.size(); i++) {
                renderDecorations(categories.get(i), matrices, i, hoverIndex, vertexConsumers);
            }
        });

        matrices.pop();
    }

    private void renderDecorations(final Category category, final MatrixStack matrices, final int index, final int hoverIndex, final VertexConsumerProvider vertexConsumers) {
        final float offsetX = (float) position.getX();
        final float offsetY = (float) position.getY();
        final double maxWidth = width.getAsDouble() - 2 * borderThickness;
        final double y = offsetY + borderThickness + index * entryHeight + index * verticalSpacing;
        final double centerY = y + entryHeight / 2.0;
        double dist = Math.abs(centerY - (pos + (height.getAsDouble() - 2 * borderThickness) / 2));
        dist *= dist * dist;
        final double offset = height.getAsDouble() / 4;
        final double scale = Math.max(offset - dist, 0) / offset;
        final boolean shadow = index == hoverIndex || selectedIndex == index;
        renderFitText(matrices, category.getName(), offsetX + borderThickness, y, maxWidth * scale, entryHeight * scale, shadow, IntRgbColour.WHITE, (int) Math.round(255 * scale), vertexConsumers);
    }

    private void renderInfo(final Category category, final VertexConsumer vertexConsumer, final MatrixStack matrices, final int index, final int hoverIndex) {
        final double offsetX = position.getX();
        final double offsetY = position.getY();
        final Matrix4f model = matrices.peek().getPositionMatrix();
        final float startX = (float) (offsetX + borderThickness);
        final float endX = (float) (offsetX + width.getAsDouble() - borderThickness);
        final float xLen = (endX - startX);
        final float startY = (float) (offsetY + borderThickness + index * entryHeight + index * verticalSpacing);
        final float endY = (float) (offsetY + borderThickness + index * entryHeight + index * verticalSpacing + entryHeight);
        final float centerY = (startY + endY) / 2f;
        final float yLen = (endY - startY);
        float dist = Math.abs(centerY - (float) (pos + (height.getAsDouble() - 2 * borderThickness) / 2));
        dist *= dist * dist;
        final float offset = ((float) height.getAsDouble()) / 4f;
        final float scale = Math.max(offset - dist, 0) / offset;
        final Colour backgroundColour = getBackgroundColour(index);
        int alpha;
        if (hoverIndex == index || selectedIndex == index) {
            alpha = 0xFF;
        } else {
            alpha = 0x77;
        }
        alpha = Math.round(alpha * scale);
        RenderUtil.colour(vertexConsumer.vertex(model, startX + xLen * scale, startY, 0), backgroundColour, alpha).next();
        RenderUtil.colour(vertexConsumer.vertex(model, startX, startY, 0), backgroundColour, alpha).next();
        RenderUtil.colour(vertexConsumer.vertex(model, startX, startY + yLen * scale, 0), backgroundColour, alpha).next();
        RenderUtil.colour(vertexConsumer.vertex(model, startX + xLen * scale, startY + yLen * scale, 0), backgroundColour, alpha).next();
    }

    private static Colour getBackgroundColour(final int index) {
        return (index & 1) == 0 ? FIRST_BACKGROUND_COLOUR : SECOND_BACKGROUND_COLOUR;
    }

    @Override
    public boolean keyPress(final int keyCode, final int scanCode, final int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_DOWN) {
            setSelectedIndex(selectedIndex + 1);
            return true;
        } else if (keyCode == GLFW.GLFW_KEY_UP) {
            setSelectedIndex(selectedIndex - 1);
            return true;
        }
        return false;
    }

    private int findHoverIndex(final double mouseX, final double mouseY) {
        final double offsetX = position.getX();
        final double offsetY = position.getY();
        final double width = this.width.getAsDouble();
        for (int index = 0; index < categories.size(); index++) {
            final double startX = offsetX + borderThickness;
            final double endX = offsetX + width - borderThickness;
            final double startY = offsetY + borderThickness + index * entryHeight + index * verticalSpacing;
            final double endY = startY + entryHeight;
            if (new Rect2d(startX, startY, endX, endY).isIn(mouseX, mouseY)) {
                return index;
            }
        }
        return -1;
    }

    private double getListHeight() {
        final int size = categories.size();
        return size * entryHeight + (size > 0 ? size - 1 : 0) * verticalSpacing;
    }

    public interface Category {
        List<ItemStackInfo> filter(List<ItemStackInfo> infos);

        Text getName();
    }
}
