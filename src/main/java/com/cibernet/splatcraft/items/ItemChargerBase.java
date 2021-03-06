package com.cibernet.splatcraft.items;

import com.cibernet.splatcraft.entities.classes.EntityChargerProjectile;
import com.cibernet.splatcraft.entities.classes.EntityInkProjectile;
import com.cibernet.splatcraft.entities.models.ModelPlayerOverride;
import com.cibernet.splatcraft.network.PacketChargeRelease;
import com.cibernet.splatcraft.network.PacketReturnChargeRelease;
import com.cibernet.splatcraft.network.SplatCraftPacketHandler;
import com.cibernet.splatcraft.utils.ColorItemUtils;
import com.cibernet.splatcraft.world.save.SplatCraftPlayerData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import java.util.UUID;

public class ItemChargerBase extends ItemWeaponBase implements ICharge
{
	private final AttributeModifier SPEED_MODIFIER;
	
	public float projectileSize;
	public float projectileSpeed;
	public int projectileLifespan;
	public float chargeSpeed;
	public float dischargeSpeed;
	public float damage;
	
	public float minConsumption;
	public float maxConsumption;
	
	private final double mobility;
	
	public ItemChargerBase(String unlocName, String registryName, float projectileSize, float projectileSpeed, int projectileLifespan, int chargeTime, int dischargeTime , float damage, float minConsumption, float maxConsumption, double mobility)
	{
		super(unlocName, registryName, maxConsumption);
		
		this.projectileSize = projectileSize;
		this.projectileLifespan = projectileLifespan;
		this.chargeSpeed = 1f/chargeTime;
		this.dischargeSpeed = 1f/dischargeTime;
		this.damage = damage;
		this.projectileSpeed = projectileSpeed;
		
		this.mobility = mobility;
		
		SPEED_MODIFIER = new AttributeModifier("Charger Mobility", mobility-1, 2).setSaved(false);
		
		this.maxConsumption = maxConsumption;
		this.minConsumption = minConsumption;
	}
	
	public ItemChargerBase(String unlocName, String registryName, ItemChargerBase parent)
	{
		this(unlocName, registryName, parent.projectileSize, parent.projectileSpeed, parent.projectileLifespan, (int) (1/parent.chargeSpeed), (int)(1/parent.dischargeSpeed), parent.damage, parent.minConsumption, parent.maxConsumption, parent.mobility);
	}
	
	public ItemChargerBase(String unlocName, String registryName, Item parent)
	{
		this(unlocName, registryName, (ItemChargerBase)parent);
	}
	
	@Override
	public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected)
	{
		super.onUpdate(stack, worldIn, entityIn, itemSlot, isSelected);
		if((entityIn instanceof EntityPlayer))
		{
			EntityPlayer player = (EntityPlayer) entityIn;
			
			//if(!player.getActiveItemStack().equals(stack))
			//	SplatCraftPlayerData.addWeaponCharge(player, stack, -dischargeSpeed);
		}
	}
	
	@Override
	public void onUsingTick(ItemStack stack, EntityLivingBase player, int count)
	{
		super.onUsingTick(stack, player, count);
	}
	
	@Override
	public void onItemTickUse(World worldIn, EntityPlayer playerIn, ItemStack stack, int useTime)
	{
		if(!SplatCraftPlayerData.getIsSquid(playerIn))
		{
			if(hasInk(playerIn, stack, getInkConsumption(SplatCraftPlayerData.getWeaponCharge(playerIn, stack))))
				SplatCraftPlayerData.addWeaponCharge(playerIn, stack, chargeSpeed);
			else playerIn.sendStatusMessage(new TextComponentTranslation("status.noInk").setStyle(new Style().setColor(TextFormatting.RED)), true);
		}
		
	}
	
	@Override
	public void onPlayerStoppedUsing(ItemStack stack, World worldIn, EntityLivingBase entityLiving, int timeLeft)
	{
		if(worldIn.isRemote && !SplatCraftPlayerData.getIsSquid((EntityPlayer) entityLiving))
		{
			UUID player = entityLiving.getUniqueID();
			float charge = SplatCraftPlayerData.getWeaponCharge(player, stack);
			if(charge > 0.05f)
			{
				SplatCraftPacketHandler.instance.sendToServer(new PacketReturnChargeRelease(charge, stack));
				SplatCraftPlayerData.setWeaponCharge(player, stack, 0);
			}
			SplatCraftPlayerData.setCanDischarge(player, true);
		}
	}
	
	@Override
	public float getDischargeSpeed()
	{
		return dischargeSpeed;
	}
	
	@Override
	public float getChargeSpeed()
	{
		return chargeSpeed;
	}
	
	@Override
	public void onRelease(World worldIn, EntityPlayer playerIn, ItemStack stack)
	{
		float charge = SplatCraftPlayerData.getWeaponCharge(playerIn, stack);
		
		EntityInkProjectile proj = new EntityChargerProjectile(worldIn, playerIn, ColorItemUtils.getInkColor(stack), charge > 0.95f ? damage : damage*charge/4f + damage/4f, (int) (projectileLifespan*charge));
		proj.shoot(playerIn, playerIn.rotationPitch, playerIn.rotationYaw, 0.0F, projectileSpeed, 0.1f);
		proj.setProjectileSize(projectileSize);
		worldIn.spawnEntity(proj);
		SplatCraftPlayerData.setWeaponCharge(playerIn, stack, 0f);
		playerIn.getCooldownTracker().setCooldown(this, 10);
		
	}
	
	@Override
	public AttributeModifier getSpeedModifier()
	{
		return SPEED_MODIFIER;
	}
	
	@Override
	public ModelPlayerOverride.EnumAnimType getAnimType()
	{
		return ModelPlayerOverride.EnumAnimType.CHARGER;
	}
	
	public float getInkConsumption(float charge)
	{
		return minConsumption + (maxConsumption-minConsumption)*charge;
	}
}
