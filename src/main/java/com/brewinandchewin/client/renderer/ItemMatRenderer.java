package com.brewinandchewin.client.renderer;

import com.brewinandchewin.common.block.ItemMatBlock;
import com.brewinandchewin.common.block.entity.ItemMatBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemEntityRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.*;

public class ItemMatRenderer implements BlockEntityRenderer<ItemMatBlockEntity> {
    public int age;

    public ItemMatRenderer(BlockEntityRendererProvider.Context pContext) {
        int ageIn = age;
    }

    @Override
    public void render(ItemMatBlockEntity cuttingBoardEntity, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
        Direction direction = cuttingBoardEntity.getBlockState().getValue(ItemMatBlock.FACING).getOpposite();
        ItemStack boardStack = cuttingBoardEntity.getStoredItem();
        int posLong = (int) cuttingBoardEntity.getBlockPos().asLong();

        if (this.age != 100000) {
            ++this.age;
        }
        if (this.age == 100000) {
            this.age = 0;
        }

        if (!boardStack.isEmpty()) {
            poseStack.pushPose();

            ItemRenderer itemRenderer = Minecraft.getInstance()
                    .getItemRenderer();
            boolean isBlockItem = itemRenderer.getModel(boardStack, cuttingBoardEntity.getLevel(), null, 0)
                    .isGui3d();

                renderItemLayingDown(poseStack, direction);


            Minecraft.getInstance().getItemRenderer().renderStatic(boardStack, ItemTransforms.TransformType.FIXED, combinedLight, combinedOverlay, poseStack, buffer, posLong);
            poseStack.popPose();
        }
    }

    public int getAge() {
        return this.age;
    }

    public float getSpin(float number) {
        return ((float)this.getAge() + number) / 20.0F;
    }

    public void renderItemLayingDown(PoseStack matrixStackIn, Direction direction) {
        // Center item above the cutting board
        matrixStackIn.translate(0.5D, 0.4D + (Mth.sin(this.getAge() / 200.0F) / 20.0F), 0.5D);
        // Rotate item to face the cutting board's front side
        float f = -direction.toYRot();
        float f3 = 3.2F * this.getSpin(1.0F);
        matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(f3));

        // Rotate item flat on the cutting board. Use X and Y from now on
        //matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(90.0F));

        // Resize the item
        matrixStackIn.scale(0.5F, 0.5F, 0.5F);
    }

    public void renderBlock(PoseStack matrixStackIn, Direction direction) {
        // Center block above the cutting board
        matrixStackIn.translate(0.5D, 0.27D, 0.5D);

        // Rotate block to face the cutting board's front side
        float f = -direction.toYRot();
        matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(f));

        // Resize the block
        matrixStackIn.scale(0.8F, 0.8F, 0.8F);
    }

    public void renderItemCarved(PoseStack matrixStackIn, Direction direction, ItemStack itemStack) {
        // Center item above the cutting board
        matrixStackIn.translate(0.5D, 0.23D, 0.5D);

        // Rotate item to face the cutting board's front side
        float f = -direction.toYRot() + 180;
        matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(f));

        // Rotate item to be carved on the surface, A little less so for hoes and pickaxes.
        Item toolItem = itemStack.getItem();
        float poseAngle;
        if (toolItem instanceof PickaxeItem || toolItem instanceof HoeItem) {
            poseAngle = 225.0F;
        } else if (toolItem instanceof TridentItem) {
            poseAngle = 135.0F;
        } else {
            poseAngle = 180.0F;
        }
        matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(poseAngle));

        // Resize the item
        matrixStackIn.scale(0.6F, 0.6F, 0.6F);
    }
}