package utility;

import game.Game.InputType;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import org.jbox2d.common.Vec2;

public class InputEventItem{
  public InputType type;
  public MouseEvent mouseEvent;
  public MouseWheelEvent mouseWheelEvent;
  public KeyEvent keyEvent;
  public Vec2 pos;
  public char c;
  public int code;

  public InputEventItem(InputType type, Vec2 pos, MouseEvent mouseEvent){
	this.type = type;
	this.pos = pos.clone();
	this.mouseEvent = mouseEvent;
  }

  public InputEventItem(InputType type, Vec2 pos, MouseWheelEvent mouseWheelEvent){
	this.type = type;
	this.pos = pos.clone();
	this.mouseWheelEvent = mouseWheelEvent;
  }

  public InputEventItem(InputType type, char c, int code, KeyEvent keyEvent){
	this.type = type;
	this.c = c;
	this.code = code;
	this.keyEvent = keyEvent;
  }
}