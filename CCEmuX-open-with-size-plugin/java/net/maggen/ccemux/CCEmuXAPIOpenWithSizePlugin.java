package net.maggen.ccemux;

import com.google.common.io.ByteStreams;
import com.sun.jdi.Method;
import dan200.computercraft.api.lua.ILuaAPI;
import dan200.computercraft.api.lua.*;
import dan200.computercraft.api.peripheral.IComputerAccess;
import net.clgd.ccemux.api.emulation.EmulatedComputer;
import net.clgd.ccemux.api.emulation.Emulator;
import net.clgd.ccemux.api.emulation.filesystem.VirtualFile;
import net.clgd.ccemux.api.plugins.Plugin;
import net.clgd.ccemux.api.plugins.PluginManager;
import net.clgd.ccemux.api.plugins.hooks.ComputerCreated;
import net.clgd.ccemux.api.plugins.hooks.CreatingROM;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;


public class CCEmuXAPIOpenWithSizePlugin extends Plugin {
	private static final Logger log = LoggerFactory.getLogger(CCEmuXAPIOpenWithSizePlugin.class);

	@FunctionalInterface
	private interface APIMethod {
		Object[] accept(ILuaContext c, Object[] o) throws LuaException, InterruptedException;
	}

	// WARNING! For all future CCEmuX plugin creators!!!
	// @LuaFunction interface is not working because of some plugin-api issues! See: https://github.com/CCEmuX/CCEmuX/issues/165
	// Use IDynamicLuaObject interface if you want your methods be accessable from CC:Tweaked environment!
	public static class API implements ILuaAPI,IDynamicLuaObject
	{
		private final Emulator emu;
		private final EmulatedComputer computer;

		public API(Emulator emu, EmulatedComputer computer) {
			this.emu = emu;
			this.computer= computer;
		}

		@Override
		public String[] getNames() {return new String[] {"ccemux_open_with_size_temp"};}
		@Override
		public String[] getMethodNames() {
			return new String[]{"openEmuWithSize"/*,"execute"*/};
		}

		@Override
		public MethodResult callMethod(ILuaContext iLuaContext, int i /* index of method in array returned by getMethodNames() */, IArguments iArguments) throws LuaException {
			Object[] obj = iArguments.getAll();
			log.info("Call Method {} {} {} {} {}",iLuaContext,i,iArguments);
			switch (i) {
				case 0: //openEmuWithSize
					var id = iArguments.optInt(0);
					var width = iArguments.optInt(1);
					var heigth = iArguments.optInt(2);
					return MethodResult.of(emu.createComputer(b -> {
						b.id(id.orElse(-1)).termSize(width.orElse(emu.getConfig().termWidth.get()),heigth.orElse(emu.getConfig().termHeight.get()));
					}).getID());
			  /*case 1: //my attempt to add os.execute
					try {
						var command = iArguments.optString(0);
						Runtime.getRuntime().exec(command.orElse("echo 1"));
					} catch (IOException e) {
						log.info(e.getMessage());
					}
					return MethodResult.of();*/
				default: //for unexpected situations
					return MethodResult.of();
			}
		}
	}

	@Nonnull
	@Override
	public String getName() {
		return "CCEmuX API Open with Size";
	}

	@Nonnull
	@Override
	public String getDescription() {
		return "Adds openEmuWithSize to 'ccemux' Lua API. That method allows user open new CCEmuX iterations with different sizes.";
	}

	@Nonnull
	@Override
	public Optional<String> getVersion() {
		return Optional.empty();
	}

	@Nonnull
	@Override
	public Collection<String> getAuthors() {
		return Collections.emptyList();
	}

	@Nonnull
	@Override
	public Optional<String> getWebsite() {
		return Optional.empty();
	}

	@Override
	public void setup(@Nonnull PluginManager manager) {
		registerHook((ComputerCreated) (emu, computer) -> {
				computer.addApi(new API(emu,computer));
		});
		registerHook((CreatingROM) (emu, romBuilder) -> {
		try {
			romBuilder.addEntry("programs/emu.lua", new VirtualFile(
				ByteStreams.toByteArray(CCEmuXAPIOpenWithSizePlugin.class.getResourceAsStream("/rom/emu_program_open_with_size.lua"))));
			romBuilder.addEntry("autorun/emu_add1.lua", new VirtualFile(
				ByteStreams.toByteArray(CCEmuXAPIOpenWithSizePlugin.class.getResourceAsStream("/rom/emu_autorun_open_with_size.lua"))));
		} catch (IOException e) {
			log.error("Failed to register ROM entries", e);
		}
		});
	}
}
