package command;

import java.util.ArrayList;
import java.util.Optional;

import facade.FileFacade;
import file.EmptyFile;
import file.File;
import file.Folder;

public class ChangeDirectoryCommand extends QueryCommand
{
	private static final int ARGUMENT_LENGTH = 1;
	private FileFacade fileFacade;

	public ChangeDirectoryCommand()
	{
		fileFacade = FileFacade.getInstance();
	}

	@Override
	public void execute()
	{
		if (!isValidArguments())
			return;

		String startPath = this.getArguments()[0].trim();
		
		if (startPath.startsWith("/"))
			startPath = "~/" + startPath.substring(1);
		
		String[] folderNames = startPath.split("/");
		
		boolean isFound = true;

		for (String folderName : folderNames)
		{
			Folder currentFolder = fileFacade.getCurrentFolder();

			if (isDotIndicator(folderName))
				fileFacade.setCurrentFolder(currentFolder);
			else if (isDoubleDotIndicator(folderName) && currentFolder.getParentFolder() != null)
				fileFacade.setCurrentFolder(currentFolder.getParentFolder());
			else if (isRootIndicator(folderName))
				fileFacade.setCurrentFolder(fileFacade.getMainFolder());
			else
			{
				ArrayList<File> sources = currentFolder.getFiles();
				File selectedFolder = this.findDirectory(sources, folderName);

				if (selectedFolder instanceof EmptyFile)
				{
					System.err.println(String.format("'%s' is not a directory.", folderName));
					return;
				}
				else if (selectedFolder == null)
				{
					isFound = false;
					break;
				}

				fileFacade.setCurrentFolder((Folder) selectedFolder);
			}
		}

		if (!isFound)
			System.err.println("No such file or directory.");
	}

	@Override
	public boolean isValidArguments()
	{
		if (hasExceedMaximunArgumentLength())
		{
			System.err.println("Too many arguments.");
			return false;
		}
		else if (hasTooShortArgumentLength())
		{
			System.err.println("Missing arguments.");
			return false;
		}

		return true;
	}

	private boolean hasExceedMaximunArgumentLength()
	{
		return this.getArguments().length > ARGUMENT_LENGTH;
	}

	private boolean hasTooShortArgumentLength()
	{
		return this.getArguments().length < ARGUMENT_LENGTH;
	}

	private boolean isDotIndicator(String folderName)
	{
		return folderName.equals(".");
	}

	private boolean isDoubleDotIndicator(String folderName)
	{
		return folderName.equals("..");
	}

	private boolean isRootIndicator(String folderName)
	{
		return folderName.equals("~");
	}

	private File findDirectory(ArrayList<File> sources, String folderName)
	{
		Optional<File> optFile = sources.stream().filter(x -> x.getName().equals(folderName)).findFirst();

		if (optFile.isPresent())
		{
			if (!(optFile.get() instanceof Folder))
				return new EmptyFile("");
			else
				return (Folder) optFile.get();
		}

		return null;
	}
}
