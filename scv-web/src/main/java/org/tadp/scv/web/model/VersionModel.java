package org.tadp.scv.web.model;

import java.io.Serializable;
import java.util.Date;

import org.tadp.scv.api.compare.Revision;
import org.tadp.scv.api.filesystem.File;
import org.tadp.scv.api.filesystem.server.FileVersionable;

@SuppressWarnings("serial")
public class VersionModel implements Serializable
{

	private Integer version;
	private Date    date;
	private String comment;
	
	private FileVersionable file;
	
	public VersionModel()
	{
		super();
	}
	
	public VersionModel(FileVersionable fileVer)
	{
		this();
		file = fileVer;
	}
	
	public VersionModel(Revision<File> revision)
	{
		this();
		setVersion(revision.getRevisionNumber());
		setDate(revision.getDate());
		setComment(revision.getComment());
	}
	public FileVersionable getFile()
	{
		return file;
	}
	public void setFile(FileVersionable file)
	{
		this.file = file;
	}
	public Integer getVersion()
	{
		return version;
	}
	public void setVersion(Integer version)
	{
		this.version = version;
	}
	public Date getDate()
	{
		return date;
	}
	public void setDate(Date date)
	{
		this.date = date;
	}
	public String getComment()
	{
		return comment;
	}
	public void setComment(String comment)
	{
		this.comment = comment;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((comment == null) ? 0 : comment.hashCode());
		result = prime * result + ((date == null) ? 0 : date.hashCode());
		result = prime * result + ((file == null) ? 0 : file.hashCode());
		result = prime * result + ((version == null) ? 0 : version.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final VersionModel other = (VersionModel) obj;
		if (comment == null)
		{
			if (other.comment != null)
				return false;
		} else if (!comment.equals(other.comment))
			return false;
		if (date == null)
		{
			if (other.date != null)
				return false;
		} else if (!date.equals(other.date))
			return false;
		if (file == null)
		{
			if (other.file != null)
				return false;
		} else if (!file.getName().equals(other.file.getName()))
			return false;
		if (version == null)
		{
			if (other.version != null)
				return false;
		} else if (!version.equals(other.version))
			return false;
		return true;
	}
	
	
}
