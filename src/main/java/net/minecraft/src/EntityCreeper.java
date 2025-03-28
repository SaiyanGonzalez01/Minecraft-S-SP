package net.minecraft.src;

public class EntityCreeper extends EntityMob {
	/**
	 * Time when this creeper was last in an active state (Messed up code here,
	 * probably causes creeper animation to go weird)
	 */
	private int lastActiveTime;

	/**
	 * The amount of time since the creeper was close enough to the player to ignite
	 */
	private int timeSinceIgnited;
	private int fuseTime = 30;

	/** Explosion radius for this creeper. */
	private int explosionRadius = 3;

	/**
	 * Returns true if the newer Entity AI code should be run
	 */
	public boolean isAIEnabled() {
		return false;
	}

	public int func_82143_as() {
		return this.getAttackTarget() == null ? 3 : 3 + (this.health - 1);
	}

	/**
	 * Called when the mob is falling. Calculates and applies fall damage.
	 */
	protected void fall(float par1) {
		super.fall(par1);
		this.timeSinceIgnited = (int) ((float) this.timeSinceIgnited + par1 * 1.5F);

		if (this.timeSinceIgnited > this.fuseTime - 5) {
			this.timeSinceIgnited = this.fuseTime - 5;
		}
	}

	public int getMaxHealth() {
		return 20;
	}

	protected void entityInit() {
		super.entityInit();
		this.dataWatcher.addObject(16, Byte.valueOf((byte) -1));
		this.dataWatcher.addObject(17, Byte.valueOf((byte) 0));
	}

	/**
	 * (abstract) Protected helper method to write subclass entity data to NBT.
	 */
	public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound) {
		super.writeEntityToNBT(par1NBTTagCompound);

		if (this.dataWatcher.getWatchableObjectByte(17) == 1) {
			par1NBTTagCompound.setBoolean("powered", true);
		}

		par1NBTTagCompound.setShort("Fuse", (short) this.fuseTime);
		par1NBTTagCompound.setByte("ExplosionRadius", (byte) this.explosionRadius);
	}

	/**
	 * (abstract) Protected helper method to read subclass entity data from NBT.
	 */
	public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound) {
		super.readEntityFromNBT(par1NBTTagCompound);
		this.dataWatcher.updateObject(17, Byte.valueOf((byte) (par1NBTTagCompound.getBoolean("powered") ? 1 : 0)));

		if (par1NBTTagCompound.hasKey("Fuse")) {
			this.fuseTime = par1NBTTagCompound.getShort("Fuse");
		}

		if (par1NBTTagCompound.hasKey("ExplosionRadius")) {
			this.explosionRadius = par1NBTTagCompound.getByte("ExplosionRadius");
		}
	}

	/**
	 * Called to update the entity's position/logic.
	 */
	public void onUpdate() {
		if (this.isEntityAlive()) {
			this.lastActiveTime = this.timeSinceIgnited;
			int var1 = this.getCreeperState();

			if (var1 > 0 && this.timeSinceIgnited == 0) {
				this.playSound("random.fuse", 1.0F, 0.5F);
			}

			this.timeSinceIgnited += var1;

			if (this.timeSinceIgnited < 0) {
				this.timeSinceIgnited = 0;
			}

			if (this.timeSinceIgnited >= this.fuseTime) {
				this.timeSinceIgnited = this.fuseTime;
			}
		}

		super.onUpdate();
	}

	/**
	 * Returns the sound this mob makes when it is hurt.
	 */
	protected String getHurtSound() {
		return "mob.creeper.say";
	}

	/**
	 * Returns the sound this mob makes on death.
	 */
	protected String getDeathSound() {
		return "mob.creeper.death";
	}

	/**
	 * Called when the mob's health reaches 0.
	 */
	public void onDeath(DamageSource par1DamageSource) {
		super.onDeath(par1DamageSource);

		if (par1DamageSource.getEntity() instanceof EntitySkeleton) {
			int var2 = Item.record13.itemID + this.rand.nextInt(Item.recordWait.itemID - Item.record13.itemID + 1);
			this.dropItem(var2, 1);
		}
	}

	public boolean attackEntityAsMob(Entity par1Entity) {
		return true;
	}

	/**
	 * Returns true if the creeper is powered by a lightning bolt.
	 */
	public boolean getPowered() {
		return this.dataWatcher.getWatchableObjectByte(17) == 1;
	}

	/**
	 * Params: (Float)Render tick. Returns the intensity of the creeper's flash when
	 * it is ignited.
	 */
	public float getCreeperFlashIntensity(float par1) {
		return ((float) this.lastActiveTime + (float) (this.timeSinceIgnited - this.lastActiveTime) * par1) / (float) (this.fuseTime - 2);
	}

	/**
	 * Returns the item ID for the item the mob drops on death.
	 */
	protected int getDropItemId() {
		return Item.gunpowder.itemID;
	}

	/**
	 * Returns the current state of creeper, -1 is idle, 1 is 'in fuse'
	 */
	public int getCreeperState() {
		return this.dataWatcher.getWatchableObjectByte(16);
	}

	/**
	 * Sets the state of creeper, -1 to idle and 1 to be 'in fuse'
	 */
	public void setCreeperState(int par1) {
		this.dataWatcher.updateObject(16, Byte.valueOf((byte) par1));
	}

	/**
	 * Called when a lightning bolt hits the entity.
	 */
	public void onStruckByLightning(EntityLightningBolt par1EntityLightningBolt) {
		super.onStruckByLightning(par1EntityLightningBolt);
		this.dataWatcher.updateObject(17, Byte.valueOf((byte) 1));
	}
}
