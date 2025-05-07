package com.oops.library.entity;
import jakarta.persistence.*;
@Entity
@DiscriminatorValue("ANCIENT")
public class AncientScript extends Book{
	
	private String originalLanguage;
	private String translationNotes;
	private boolean archived=true;
	
	private String manuscriptPath;
	
	
	
	
	public String getManuscriptPath() {
		return manuscriptPath;
	}
	public boolean isArchived() {
		return archived;
	}
	public void setArchived(boolean archived) {
		this.archived = archived;
	}
	public String getOriginalLanguage() {
		return originalLanguage;
	}
	public void setOriginalLanguage(String originalLanguage) {
		this.originalLanguage = originalLanguage;
	}
	public String getTranslationNotes() {
		return translationNotes;
	}
	public void setTranslationNotes(String translationNotes) {
		this.translationNotes = translationNotes;
	}
	public void setManuscriptPath(String manuscriptPath) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public String getType() {
		return "ANCIENT";
	}
	
	

}
