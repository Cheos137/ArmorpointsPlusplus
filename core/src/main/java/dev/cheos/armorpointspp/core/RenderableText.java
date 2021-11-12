package dev.cheos.armorpointspp.core;

import java.util.ArrayList;
import java.util.List;

import dev.cheos.armorpointspp.core.adapter.IPoseStack;
import dev.cheos.armorpointspp.core.adapter.IRenderer;
import dev.cheos.armorpointspp.core.adapter.IRenderer.TextRenderType;

public class RenderableText {
	public static final RenderableText SPACE = new RenderableText(" ");
	
	private RenderableText parent;
	private float padLeft, padRight;
	private final String text;
	private final Properties properties = new Properties();
	private final List<RenderableText> children = new ArrayList<>();

	public RenderableText(Object text) { this.text = String.valueOf(text); }
	public RenderableText(String text) { this.text = text; }
	
	public RenderableText append(RenderableText text) {
		text.parent = this;
		this.children.add(text);
		return this;
	}
	
	public RenderableText withShadow(boolean shadow) {
		this.properties.shadow.set(shadow);
		return this;
	}
	
	public RenderableText withColor(int color) {
		this.properties.color.set(color);
		return this;
	}
	
	public RenderableText withAlignment(Alignment alignment) {
		if (this.parent != null) {
			this.parent.withAlignment(alignment);
			return this;
		}

		this.properties.alignment.set(alignment);
		return this;
	}
	
	public RenderableText pad(float padding) {
		this.padLeft  = padding;
		this.padRight = padding;
		return this;
	}
	
	public RenderableText padLeft(float padLeft) {
		this.padLeft = padLeft;
		return this;
	}
	
	public RenderableText padRight(float padRight) {
		this.padRight = padRight;
		return this;
	}
	
	public boolean hasShadow() {
		return this.properties.shadow.get(this.parent != null ? this.parent.hasShadow() : true);
	}
	
	public int getColor() {
		return this.properties.color.get(this.parent != null ? this.parent.getColor() : 0xffffff);
	}
	
	public Alignment getOrientation() {
		return this.parent != null ? this.parent.getOrientation() : this.properties.alignment.get(Alignment.LEFT);
	}
	
	private float width(IRenderer renderer) {
		float w = renderer.width(this.text) + this.padLeft + this.padRight;
		for (RenderableText t : this.children)
			w += t.width(renderer);
		return w;
	}
	
	
	public void render(IPoseStack poseStack, IRenderer renderer, float x, float y) {
		if (this.parent != null) {
			this.parent.render(poseStack, renderer, x, y);
			return;
		}
		draw(poseStack, renderer, new Position(x, y));
	}
	
	protected void draw(IPoseStack poseStack, IRenderer renderer, Position pos) {
		if (this.parent == null)
			switch (getOrientation()) {
				case LEFT:
					break;
				case CENTER:
					pos.x -= width(renderer) / 2;
					break;
				case RIGHT:
					pos.x -= width(renderer);
					break;
			}
		
		renderer.text(poseStack, this.text, pos.x - this.padLeft, pos.y, getColor(), TextRenderType.TEXT, hasShadow());
		pos.x += renderer.width(this.text) + this.padLeft + this.padRight;
		this.children.forEach(c -> c.draw(poseStack, renderer, pos));
	}
	
	@Override
	public String toString() {
		String text = this.text;
		for (RenderableText t : this.children)
			text += t.toString();
		return text;
	}
	
	
	private static final class Position {
		private float x, y;
		private Position(float x, float y) { this.x = x; this.y = y; }
	}
	
	private static final class Properties {
		private final PropertyState<Boolean>   shadow    = new PropertyState<>();
		private final PropertyState<Integer>   color     = new PropertyState<>();
		private final PropertyState<Alignment> alignment = new PropertyState<>();
		
		private static final class PropertyState<T> {
			private boolean set;
			private T value;
			
			private PropertyState() { this.set = false; }
			
			private PropertyState(T value) {
				this.set = value != null;
				this.value = this.set ? value : null;
			}
			
			private void set(T value) {
				this.set = value != null;
				this.value = this.set ? value : null;
			}
			
			private T get(T def) {
				return this.set ? this.value : def;
			}
		}
	}
	
	public static enum Alignment {
		LEFT,
		CENTER,
		RIGHT;
	}
}
