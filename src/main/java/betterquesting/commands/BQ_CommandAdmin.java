package betterquesting.commands;

import betterquesting.commands.admin.*;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class BQ_CommandAdmin extends CommandBase
{
	ArrayList<QuestCommandBase> coms = new ArrayList<QuestCommandBase>();
	
	public BQ_CommandAdmin()
	{
		coms.add(new QuestCommandEdit());
		coms.add(new QuestCommandHardcore());
		coms.add(new QuestCommandReset());
		coms.add(new QuestCommandComplete());
		coms.add(new QuestCommandDelete());
		coms.add(new QuestCommandDefaults());
		coms.add(new QuestCommandLives());
	}
	
	@Override
	public String getName()
	{
		return "bq_admin";
	}
	
	@Override
	public String getUsage(ICommandSender sender)
	{
		String txt = "";
		
		for(int i = 0; i < coms.size(); i++)
		{
			QuestCommandBase c = coms.get(i);
			txt += "/bq_admin " + c.getCommand();
			
			if(c.getUsageSuffix().length() > 0)
			{
				txt += " " + c.getUsageSuffix();
			}
			
			if(i < coms.size() -1)
			{
				txt += ", ";
			}
		}
		
		return txt;
	}

    /**
     * Adds the strings available in this command to the given list of tab completion options.
     */
	@Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] strings, BlockPos pos)
    {
		if(strings.length == 1)
		{
			ArrayList<String> base = new ArrayList<String>();
			for(QuestCommandBase c : coms)
			{
				base.add(c.getCommand());
			}
        	return getListOfStringsMatchingLastWord(strings, base.toArray(new String[0]));
		} else if(strings.length > 1)
		{
			for(QuestCommandBase c : coms)
			{
				if(c.getCommand().equalsIgnoreCase(strings[0]))
				{
					return c.autoComplete(server, sender, strings);
				}
			}
		}
		
		return new ArrayList<String>();
    }
	
	@Override
    public int getRequiredPermissionLevel()
    {
        return 2;
    }

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		if(args.length < 1)
		{
			throw new WrongUsageException(this.getUsage(sender));
		}
		
		for(QuestCommandBase c : coms)
		{
			if(c.getCommand().equalsIgnoreCase(args[0]))
			{
				if(c.validArgs(args))
				{
					c.runCommand(server, this, sender, args);
					return;
				} else
				{
					throw c.getException(this);
				}
			}
		}
		
		throw new WrongUsageException(this.getUsage(sender));
	}

    /**
     * Return whether the specified command parameter index is a username parameter.
     */
	@Override
    public boolean isUsernameIndex(String[] args, int index)
    {
		if(args.length < 1)
		{
			return false;
		}
		
		for(QuestCommandBase c : coms)
		{
			if(c.getCommand().equalsIgnoreCase(args[0]))
			{
				return c.isArgUsername(args, index);
			}
		}
		
		return false;
    }
}
