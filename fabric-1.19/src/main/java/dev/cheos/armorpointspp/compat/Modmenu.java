package dev.cheos.armorpointspp.compat;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

import dev.cheos.armorpointspp.config.ApppConfig;
import dev.cheos.armorpointspp.config.ApppConfigValue;
import dev.cheos.armorpointspp.config.ApppConfigValue.*;
import dev.cheos.armorpointspp.core.adapter.IConfig;
import dev.cheos.armorpointspp.mixin.AbstractWidgetMixin;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public class Modmenu implements ModMenuApi {
	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return ModmenuScreen::new;
	}
	
	
	public static class ModmenuScreen extends Screen {
		private final Screen prev;
		private int selectedCategory = 0;
		private List<TabButton> tabs = new ArrayList<>();
		private List<EntryList> tabContents = new ArrayList<>();
		
		public ModmenuScreen(Screen prev) {
			super(Component.translatable("armorpointspp.modmenu.title"));
			this.prev = prev;
		}
		
		@Override
		protected void init() {
			if (this.tabs.isEmpty()) {
				List<? extends Component> names = Arrays.stream(IConfig.Category.values())
						.filter(cat -> cat.hasOptions(ApppConfig.VERSION))
						.map(cat -> Component.translatable("armorpointspp.tab." + cat.getPathJoined()))
						.toList();
				List<Integer> widths = names.stream().map(comp -> this.font.width(comp) + 10).toList();
				List<Integer> cmlWidths = new ArrayList<>(widths);
				
				cmlWidths.set(0, cmlWidths.get(0) + 4);
				for (int i = 1; i < cmlWidths.size(); i++)
					cmlWidths.set(i, cmlWidths.get(i - 1) + cmlWidths.get(i) + 4);
				cmlWidths.set(cmlWidths.size() - 1, cmlWidths.get(cmlWidths.size() - 1) - 4);
				
				int xoff = -cmlWidths.get(cmlWidths.size() - 1) / 2;
				
				int i = 0;
				for (IConfig.Category cat : IConfig.Category.values()) {
					if (!cat.hasOptions(ApppConfig.VERSION)) { continue; }
					
					TabButton tb = new TabButton(i, this.width / 2 + xoff + (i == 0 ? 0 : cmlWidths.get(i-1)), 20, widths.get(i), 20, names.get(i), List.of());
					this.tabs.add(tb);
					
					EntryList list = new EntryList(this.minecraft, this.width, this.height, 50, this.height - 50, 25);
					this.tabContents.add(list);
					
					for (IConfig.Option<?> opt : cat.getOptions())
						if (opt.isAvailableIn(ApppConfig.VERSION)) {
							final ApppConfigValue<?, ?, ?> val = ApppConfig.findValue(opt);
							WidgetProvider widget = null;
							
							if (val instanceof BoolValue bv)
								widget = new TrueFalseOption(bv::set, bv::get);
							else if (val instanceof HexValue hv)
								widget = new EditOption(hv::set, hv::getHex);
							else if (val instanceof StringValue sv)
								widget = new EditOption(sv::set, sv::get);
							else if (val instanceof FloatValue fv) {
								if (fv.min != 0 && fv.max != Float.MAX_VALUE && fv.min < fv.max)
									widget = new SliderOption(fv.min, fv.max, 0.1f, fv::set, fv::get);
								else widget = new EditOption(fv::set, () -> fv.get().toString());
							} else if (val instanceof EnumValue<?> ev)
								widget = new EnumCycleOption(ev::set, () -> ev.get().name(), Arrays.stream(ev.type.getEnumConstants()).map(Enum::name).toList());
							
							list.addSmall(new TextOption(opt.key(), opt.comments()), widget);
						}
					i++;
				}
			}
			
			addWidget(this.tabContents.get(this.selectedCategory));
			
			for (TabButton tb : this.tabs)
				addRenderableWidget(tb);
			
			addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, (btn) -> {
				ApppConfig.save();
				this.minecraft.setScreen(this.prev);
			}).bounds(this.width / 2 + 004, this.height - 28, 150, 20).build());
			addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, (btn) -> {
				ApppConfig.load();
				this.minecraft.setScreen(this.prev);
			}).bounds(this.width / 2 - 154, this.height - 28, 150, 20).build());
		}
		
		@Override
		public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
			renderBackground(poseStack);
			this.tabContents.get(this.selectedCategory).render(poseStack, mouseX, mouseY, partialTicks);
			drawCenteredString(poseStack, this.font, this.title, this.width / 2, 5, 0xFFFFFF);
			super.render(poseStack, mouseX, mouseY, partialTicks);
			this.tabContents.get(this.selectedCategory)
					.getMouseOver(mouseX, mouseY)
					.ifPresent(w -> {
							if (((AbstractWidgetMixin) w).getTooltip() instanceof Tooltip tooltip)
								renderTooltip(poseStack, tooltip.toCharSequence(this.minecraft), mouseX, mouseY); // TODO fix width in 1.19.3
					});
		}
		
		@Override
		public void onClose() {
			ApppConfig.load();
			super.onClose();
		}
		
		
		
		public class TabButton extends AbstractButton {
			private final int index;
			private final List<Component> tooltip;
			
			public TabButton(int index, int x, int y, int w, int h, Component component, List<Component> tooltip) {
				super(x, y, w, h, component);
				this.index = index;
				this.tooltip = tooltip;
			}
			
			public TabButton(int index, int x, int y, int w, int h, Component component, String[] tooltip) {
				this(index, x, y, w, h, component, new ArrayList<>(tooltip.length));
				for (String line : tooltip)
					this.tooltip.add(Component.literal(line));
			}
			
			@Override
			public void onPress() {
				if (this.index != -1)
					ModmenuScreen.this.selectedCategory = this.index;
				ModmenuScreen.this.init(Minecraft.getInstance(), ModmenuScreen.this.width, ModmenuScreen.this.height);
			}
			
			@Override
			public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
				this.active = this.index != ModmenuScreen.this.selectedCategory;
				super.render(poseStack, mouseX, mouseY, partialTicks);
				
				if (isMouseOver(mouseX, mouseY))
					if (this.tooltip != null && this.tooltip.size() > 0)
						ModmenuScreen.this.renderComponentTooltip(poseStack, this.tooltip, mouseX, mouseY);
			}
			
			@Override
			protected boolean clicked(double mx, double my) {
				return this.visible && this.active && isMouseOver(mx, my);
			}
			
			@Override
			public boolean isMouseOver(double mx, double my) {
				return this.visible
						&& mx >= getX() && my >= getY()
						&& mx < getX() + this.width && my < getY() + this.height
						&& mx >= 20 && mx < ModmenuScreen.this.width - 20;
			}
			
			@Override
			protected void updateWidgetNarration(NarrationElementOutput elem) { }
		}
		
		
		public class TextWidget extends AbstractWidget {
			public TextWidget(int x, int y, int w, int h, Component text, Component tooltip) {
				super(x, y, w, h, text);
				setTooltip(Tooltip.create(tooltip));
				this.active = false;
			}
			
			@Override
			public void render(PoseStack poseStack, int mx, int my, float partialTicks) {
		        AbstractWidget.drawCenteredString(poseStack, ModmenuScreen.this.font, getMessage(), getX() + this.width / 2, getY() + (this.height - 8) / 2, 0xFFFFFF | Mth.ceil(this.alpha * 255.0f) << 24);
			}
			
			@Override
			public boolean isMouseOver(double mx, double my) {
				return this.visible
						&& mx >= getX() && my >= getY()
						&& mx < getX() + this.width && my < getY() + this.height;
			}
			
			@Override
			protected void updateWidgetNarration(NarrationElementOutput elem) { }
		}
		
		
		public class TextOption implements WidgetProvider {
			private final Component text;
			private final Component tooltip;
			
			public TextOption(String text, String[] tooltip) {
				this.text = Component.literal(text);
				this.tooltip = Component.literal(String.join("\n", tooltip));
			}
			
			@Override
			public AbstractWidget createButton(int x, int y, int w) { // h = 20
				return new TextWidget(x, y, w, 20, this.text, this.tooltip);
			}
		}
		
		
		public class EditOption implements WidgetProvider {
			private final Consumer<String> onChange;
			private final Supplier<String> provider;
			
			public EditOption(Consumer<String> onChange, Supplier<String> provider) {
				this.onChange = onChange;
				this.provider = provider;
			}
			
			@Override
			public AbstractWidget createButton(int x, int y, int w) { // h = 20
				EditBox editBox = new EditBox(ModmenuScreen.this.font, x, y, w, 20, Component.literal(""));
				editBox.setResponder(this.onChange);
				editBox.setValue(this.provider.get());
				return editBox;
			}
		}
		
		public class TrueFalseOption implements WidgetProvider {
			private final Consumer<Boolean> onChange;
			private final Supplier<Boolean> provider;
			
			public TrueFalseOption(Consumer<Boolean> onChange, Supplier<Boolean> provider) {
				this.onChange = onChange;
				this.provider = provider;
			}
			
			@Override
			public AbstractWidget createButton(int x, int y, int w) { // h = 20
				return CycleButton.onOffBuilder().withInitialValue(this.provider.get()).displayOnlyValue().create(x, y, w, 20, Component.literal(""), (btn, b) -> this.onChange.accept(b));
			}
		}
		
		public class EnumCycleOption implements WidgetProvider {
			private final Consumer<String> onChange;
			private final Supplier<String> provider;
			private final List<String> values;
			
			public EnumCycleOption(Consumer<String> onChange, Supplier<String> provider, List<String> values) {
				this.onChange = onChange;
				this.provider = provider;
				this.values = values;
			}
			
			@Override
			public AbstractWidget createButton(int x, int y, int w) { // h = 20
				return CycleButton.<String>builder(s -> Component.literal(s))
						.withInitialValue(this.provider.get())
						.withValues(this.values)
						.displayOnlyValue()
						.create(x, y, w, 20, Component.literal(""), (btn, s) -> this.onChange.accept(s));
			}
		}
		
		public class SliderWidget extends AbstractOptionSliderButton {
			private final float min, max, step;
			private final Consumer<Float> onChange;
			private final Supplier<Float> provider;
			
			protected SliderWidget(int x, int y, int w, int h, float min, float max, float step, Consumer<Float> onChange, Supplier<Float> provider) {
				super(ModmenuScreen.this.minecraft.options, x, y, w, h, toPct(provider.get(), min, max, step));
				this.min = min;
				this.max = max;
				this.step = step;
				this.onChange = onChange;
				this.provider = provider;
				updateMessage();
			}
			
			@Override
			protected void updateMessage() {
				setMessage(Component.literal(String.valueOf(this.provider.get())));
			}
			
			@Override
			protected void applyValue() {
				this.onChange.accept(toValue((float) this.value));
			}
			
			public float toPct(float val) {
				return Mth.clamp((clamp(val) - this.min) / (this.max - this.min), 0, 1);
			}
			
			public static float toPct(float val, float min, float max, float step) {
				return Mth.clamp((clamp(val, min, max, step) - min) / (max - min), 0, 1);
			}
			
			public float toValue(float val) {
				return clamp(Mth.lerp(Mth.clamp(val, 0, 1), this.min, this.max));
			}
			
			public static float toValue(float val, float min, float max, float step) {
				return clamp(Mth.lerp(Mth.clamp(val, 0, 1), min, max), min, max, step);
			}
			
			public float clamp(float val) {
				if (this.step > 0)
					val = this.step * Math.round(val / this.step);
				return Mth.clamp(val, this.min, this.max);
			}
			
			private static float clamp(float val, float min, float max, float step) {
				if (step > 0)
					val = step * Math.round(val / step);
				return Mth.clamp(val, min, max);
			}
		}
		
		public class SliderOption implements WidgetProvider {
			private final float min, max, step;
			private final Consumer<Float> onChange;
			private final Supplier<Float> provider;
			
			public SliderOption(float min, float max, float step, Consumer<Float> onChange, Supplier<Float> provider) {
				this.min = min;
				this.max = max;
				this.step = step;
				this.onChange = onChange;
				this.provider = provider;
			}
			
			@Override
			public AbstractWidget createButton(int x, int y, int w) {
				return new SliderWidget(x, y, w, 20, this.min, this.max, this.step, onChange, provider);
			}
		}
		
		
		public static interface WidgetProvider {
			AbstractWidget createButton(int x, int y, int w);
		}
		
		
		public static class EntryList extends ContainerObjectSelectionList<EntryList.Entry> {
			public EntryList(Minecraft minecraft, int i, int j, int k, int l, int m) {
				super(minecraft, i, j, k, l, m);
				this.centerListVertically = false;
			}
			
			public void addBig(WidgetProvider widget) {
				addEntry(EntryList.Entry.big(this.width, widget));
			}
			
			public void addSmall(WidgetProvider left, WidgetProvider right) {
				addEntry(EntryList.Entry.small(this.width, left, right));
			}
			
			@Override
			public int getRowWidth() {
				return 400;
			}
			
			@Override
			protected int getScrollbarPosition() {
				return super.getScrollbarPosition() + 32;
			}
			
			public Optional<AbstractWidget> getMouseOver(double mx, double my) {
				for (EntryList.Entry entry : children())
					for (AbstractWidget widget : entry.children)
						if (widget.isMouseOver(mx, my))
							return Optional.of(widget);
				return Optional.empty();
			}
			
			
			public static class Entry extends ContainerObjectSelectionList.Entry<EntryList.Entry> {
				private final List<AbstractWidget> children;
				
				private Entry(List<AbstractWidget> children) {
					this.children = children;
				}
				
				public static EntryList.Entry big(int width, WidgetProvider provider) {
					return new EntryList.Entry(ImmutableList.of(provider.createButton(width / 2 - 155, 0, 310)));
				}
				
				public static EntryList.Entry small(int width, WidgetProvider left, WidgetProvider right) {
					AbstractWidget widget = left.createButton(width / 2 - 155, 0, 150);
					return right == null
							? new EntryList.Entry(ImmutableList.of(widget))
							: new EntryList.Entry(ImmutableList.of(widget, right.createButton(width / 2 + 5, 0, 150)));
				}
				
				@Override
				public List<? extends GuiEventListener> children() {
					return this.children;
				}
				
				@Override
				public List<? extends NarratableEntry> narratables() {
					return this.children;
				}
				
				@Override
				public void render(PoseStack poseStack, int i, int j, int k, int l, int m, int n, int o, boolean bl, float f) {
					this.children.forEach(w -> {
						w.setY(j);
						w.render(poseStack, n, o, f);
					});
				}
			}
		}
	}
}
