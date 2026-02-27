package com.arabicprose.presentation;

//Add this interface to your project
public interface IAuthorChangeObserver {
 void onAuthorDeleted(int authorId);
 void onAuthorAdded();
 void onAuthorUpdated();
}