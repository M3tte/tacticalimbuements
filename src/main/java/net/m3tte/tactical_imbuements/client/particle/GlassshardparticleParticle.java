
package net.m3tte.tactical_imbuements.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;

public class GlassshardparticleParticle extends TextureSheetParticle {
	public static GlassshardparticleParticleProvider provider(SpriteSet spriteSet) {
		return new GlassshardparticleParticleProvider(spriteSet);
	}

	public static class GlassshardparticleParticleProvider implements ParticleProvider<SimpleParticleType> {
		private final SpriteSet spriteSet;

		public GlassshardparticleParticleProvider(SpriteSet spriteSet) {
			this.spriteSet = spriteSet;
		}

		public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
			return new GlassshardparticleParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, this.spriteSet);
		}
	}

	private final SpriteSet spriteSet;

	private float angularVelocity;
	private float angularAcceleration;

	protected GlassshardparticleParticle(ClientLevel world, double x, double y, double z, double vx, double vy, double vz, SpriteSet spriteSet) {
		super(world, x, y, z);
		this.spriteSet = spriteSet;
		this.setSize(0.35f, 0.35f);

		this.lifetime = 200;
		this.gravity = 0.8f;
		this.hasPhysics = true;
		this.xd = vx * 1;
		this.yd = vy * 1;
		this.zd = vz * 1;

		this.roll = (float)Math.toRadians((double)(this.random.nextFloat() * 360.0F));
		this.oRoll = roll;
		this.angularVelocity = this.random.nextFloat() * 0.2f - 0.1f;
		this.angularAcceleration = 0f;
		this.pickSprite(spriteSet);
	}

	@Override
	public ParticleRenderType getRenderType() {
		return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
	}

	@Override
	public void tick() {
		super.tick();
		this.oRoll = this.roll;
		this.roll += this.angularVelocity;
		if (this.onGround) {
			this.angularVelocity = 0;
			this.roll = (float)Math.toRadians((double)(this.roll * 57.295779513 - ((this.roll * 57.295779513) % 45) + 22));
		}
		this.angularVelocity += this.angularAcceleration;
	}
}
