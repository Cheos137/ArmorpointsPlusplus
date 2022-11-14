package dev.cheos.armorpointspp.compat;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.mojang.blaze3d.vertex.PoseStack;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

import dev.cheos.armorpointspp.config.ApppConfig;
import dev.cheos.armorpointspp.config.ApppConfigValue;
import dev.cheos.armorpointspp.config.ApppConfigValue.*;
import dev.cheos.armorpointspp.core.adapter.IConfig;
import net.minecraft.client.*;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.*;
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
		private List<OptionsList> tabContents = new ArrayList<>();
		
		public ModmenuScreen(Screen prev) {
			super(new TranslatableComponent("armorpointspp.modmenu.title"));
			this.prev = prev;
		}
		
		@Override
		protected void init() {
			if (this.tabs.isEmpty()) {
				List<? extends Component> names = Arrays.stream(IConfig.Category.values())
						.filter(cat -> cat.hasOptions(ApppConfig.VERSION))
						.map(cat -> new TranslatableComponent("armorpointspp.tab." + cat.getPathJoined()))
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
					
					OptionsList list = new OptionsList(this.minecraft, this.width, this.height, 50, this.height - 50, 25);
					this.tabContents.add(list);
					
					for (IConfig.Option<?> opt : cat.getOptions())
						if (opt.isAvailableIn(ApppConfig.VERSION)) {
							final ApppConfigValue<?, ?, ?> val = ApppConfig.findValue(opt);
							Option widget = null;
							
							if (val instanceof BoolValue bv)
								widget = new TrueFalseOption(bv::set, bv::get);
							else if (val instanceof HexValue hv)
								widget = new EditOption(hv::set, hv::getHex);
							else if (val instanceof StringValue sv)
								widget = new EditOption(sv::set, sv::get);
							else if (val instanceof FloatValue fv) {
								if (fv.min != 0 && fv.max != Float.MAX_VALUE && fv.min < fv.max)
									widget = new ProgressOption("", fv.min, fv.max, 0.1f, opts -> fv.get().doubleValue(), (opts, d) -> fv.set(d.floatValue()), (opts, o) -> new TextComponent(String.valueOf(fv.get())));
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
			
			addRenderableWidget(new Button(this.width / 2 + 004, this.height - 28, 150, 20, CommonComponents.GUI_DONE, (btn) -> {
				ApppConfig.save();
				this.minecraft.setScreen(this.prev);
			}));
			addRenderableWidget(new Button(this.width / 2 - 154, this.height - 28, 150, 20, CommonComponents.GUI_CANCEL, (btn) -> {
				ApppConfig.load();
				ApppConfig.save();
				this.minecraft.setScreen(this.prev);
			}));
		}
		
		@Override
		public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
			renderBackground(poseStack);
			this.tabContents.get(this.selectedCategory).render(poseStack, mouseX, mouseY, partialTicks);
			drawCenteredString(poseStack, this.font, this.title, this.width / 2, 5, 0xFFFFFF);
			super.render(poseStack, mouseX, mouseY, partialTicks);
			this.tabContents.get(this.selectedCategory).getMouseOver(mouseX, mouseY).ifPresent(w -> w.renderToolTip(poseStack, mouseX, mouseY));
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
					this.tooltip.add(new TextComponent(line));
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
						&& mx >= this.x && my >= this.y
						&& mx < this.x + this.width && my < this.y + this.height
						&& mx >= 20 && mx < ModmenuScreen.this.width - 20;
			}
			
			@Override public void updateNarration(NarrationElementOutput var1) { }
		}
		
		
		public class TextWidget extends AbstractWidget {
			private final List<Component> tooltip;
			
			public TextWidget(int x, int y, int w, int h, Component text, List<Component> tooltip) {
				super(x, y, w, h, text);
				this.tooltip = tooltip;
				this.active = false;
			}
			
			@Override
			public void render(PoseStack poseStack, int mx, int my, float partialTicks) {
		        AbstractWidget.drawCenteredString(poseStack, ModmenuScreen.this.font, getMessage(), this.x + this.width / 2, this.y + (this.height - 8) / 2, 0xFFFFFF | Mth.ceil(this.alpha * 255.0f) << 24);
			}
			
			@Override
			public boolean isMouseOver(double mx, double my) {
				return this.visible
						&& mx >= this.x && my >= this.y
						&& mx < this.x + this.width && my < this.y + this.height;
			}
			
			@Override
			public void renderToolTip(PoseStack poseStack, int mx, int my) {
				ModmenuScreen.this.renderTooltip(poseStack, this.tooltip, Optional.empty(), mx, my);
			}
			
			@Override public void updateNarration(NarrationElementOutput var1) { }
		}
		
		
		public class TextOption extends Option {
			private final List<? extends Component> tooltip;
			
			public TextOption(String text, String[] tooltip) {
				super(text);
				this.tooltip = Arrays.stream(tooltip).map(TextComponent::new).toList();
			}
			
			@Override
			@SuppressWarnings("unchecked")
			public AbstractWidget createButton(Options options, int x, int y, int w) { // h = 20
				return new TextWidget(x, y, w, 20, getCaption(), (List<Component>) this.tooltip);
			}
		}
		
		
		public class EditOption extends Option {
			private final Consumer<String> onChange;
			private final Supplier<String> provider;
			
			public EditOption(Consumer<String> onChange, Supplier<String> provider) {
				super("");
				this.onChange = onChange;
				this.provider = provider;
			}
			
			@Override
			public AbstractWidget createButton(Options options, int x, int y, int w) { // h = 20
				EditBox editBox = new EditBox(ModmenuScreen.this.font, x, y, w, 20, new TextComponent(""));
				editBox.setResponder(this.onChange);
				editBox.setValue(this.provider.get());
				return editBox;
			}
		}
		
		public class TrueFalseOption extends Option {
			private final Consumer<Boolean> onChange;
			private final Supplier<Boolean> provider;
			
			public TrueFalseOption(Consumer<Boolean> onChange, Supplier<Boolean> provider) {
				super("");
				this.onChange = onChange;
				this.provider = provider;
			}
			
			@Override
			public AbstractWidget createButton(Options options, int x, int y, int w) { // h = 20
				return CycleButton.onOffBuilder().withInitialValue(this.provider.get()).displayOnlyValue().create(x, y, w, 20, new TextComponent(""), (btn, b) -> this.onChange.accept(b));
			}
		}
		
		public class EnumCycleOption extends Option {
			private final Consumer<String> onChange;
			private final Supplier<String> provider;
			private final List<String> values;
			
			public EnumCycleOption(Consumer<String> onChange, Supplier<String> provider, List<String> values) {
				super("");
				this.onChange = onChange;
				this.provider = provider;
				this.values = values;
			}
			
			@Override
			public AbstractWidget createButton(Options options, int x, int y, int w) { // h = 20
				return CycleButton.<String>builder(s -> new TextComponent(s))
						.withInitialValue(this.provider.get())
						.withValues(this.values)
						.displayOnlyValue()
						.create(x, y, w, 20, new TextComponent(""), (btn, s) -> this.onChange.accept(s));
			}
		}
	}
}
