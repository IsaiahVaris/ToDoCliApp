package com.decagon;

import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;

public class ToDoCliApp {

	private static final String LIST = "list";
	private static final String ADD = "add";
	private static final String COMPLETE = "complete";
	private static final String DELETE = "delete";

	private static int listCount;

	private static List<String> todo = new ArrayList<>();

	private static boolean updated = false;

	public static String env = "main";

	public static List<String> getTodo() {
		return todo;
	}

	public static void setTodo(final List<String> todo) {
		ToDoCliApp.todo = todo;
	}

	public static void main(String[] args) {
		if (args.length > 0) {
			readFromFile();
			String command = args[0].toLowerCase();

			switch (command) {
				case LIST:
					processListCommand();
					break;
				case ADD:
					processAddCommand(args);
					break;
				case COMPLETE:
					processCompleteCommand(args);
					break;
				case DELETE:
					processDeleteCommand(args);
					break;
				default:
					System.err.println("Please specify a valid command.");
			}
			if (updated) {
				writeToFile();
			}
		}
	}

	static void writeToFile() {
		try (FileWriter fileWriter = new FileWriter("src/" + env + "/resources/todo.json")) {
			fileWriter.write(new ObjectMapper().writeValueAsString(todo));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static void readFromFile() {
		try (FileReader fileReader = new FileReader("src/" + env + "/resources/todo.json")) {
			todo = new ObjectMapper().readValue(fileReader, List.class);
			listCount = todo.size();
		} catch (MismatchedInputException ignored) {

		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	static void processListCommand() {
		if(!todo.isEmpty()) {
			StringBuilder sb = new StringBuilder();
			int count = 0;
			for (String item : todo) {
				sb.append(++count).append(". ").append(item).append("\n");
			}
			System.out.print(sb);
		} else {
			System.out.println("No items in todo list.");
		}
	}

	static void processAddCommand(String[] args) {
		if (args.length > 1) {
			String value = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
			todo.add(value);
			updated = true;
		} else {
			System.err.println("Please specify the item to be added to the list like this: add <todo list item>");
		}
	}

	static void processDeleteCommand(String[] args) {
		if (args.length == 2) {
			try {
				int i = Integer.parseInt(args[1]) - 1;

				if (listCount > 0) {
					if (i < 0 || i >= listCount) {
						System.err.println("Please specify item number present in the list to delete.");
					} else {
						todo.remove(i);
						updated = true;
					}
				} else {
					System.out.println("No items in todo list.");
				}
			} catch (NumberFormatException nfe) {
				System.err.println("Please specify the item to be deleted from the list like this: delete <number of item in the list>");
			}
		} else {
			System.err.println("Please specify the item to be deleted from the list like this: delete <number of item in the list>");
		}

	}

	static void processCompleteCommand(String[] args) {
		if (args.length == 2) {
			try {
				int i = Integer.parseInt(args[1]) - 1;

				if (i < 0 || i > listCount) {
					System.err.println("Please specify item number present in the list to complete.");
				} else {
					String item = todo.get(i);
					if (!item.endsWith("(complete")) {
						todo.set(i, item + " (complete)");
						updated = true;
					}
				}
			} catch (NumberFormatException nfe) {
				System.err.println(
					"Please specify the item to be completed from the list like this: complete <number of item in the list>");
			}
		} else {
			System.err.println("Please specify the item to be completed from the list like this: complete <number of item in the list>");
		}
	}

}
