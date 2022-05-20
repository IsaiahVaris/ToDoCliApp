package com.decagon;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.FileWriter;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

class ToDoCliAppTest {


	private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
	private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
	private final PrintStream originalOut = System.out;
	private final PrintStream originalErr = System.err;


	@BeforeAll
	static void setUp() {
		ToDoCliApp.env = "test";
	}

	@AfterEach
	void after() {
		ToDoCliApp.getTodo().clear();
		System.setOut(originalOut);
		System.setErr(originalErr);
	}

	@BeforeEach
	void before() {
		System.setOut(new PrintStream(outContent));
		System.setErr(new PrintStream(errContent));
		ToDoCliApp.getTodo().addAll(List.of("item1", "item2"));
		try (FileWriter fileWriter = new FileWriter("src/test/resources/todo.json")) {
			fileWriter.write(new ObjectMapper().writeValueAsString(ToDoCliApp.getTodo()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	void testReadAndWriteToFile() {
		ToDoCliApp.setTodo(List.of("item1", "item2", "item3"));
		ToDoCliApp.writeToFile();
		ToDoCliApp.setTodo(new ArrayList<>());

		List<String> toDoList = ToDoCliApp.getTodo();

		assertEquals(0, toDoList.size());

		ToDoCliApp.readFromFile();
		toDoList = ToDoCliApp.getTodo();

		assertEquals(3, toDoList.size());

		assertEquals("item1", toDoList.get(0));
		assertEquals("item2", toDoList.get(1));
		assertEquals("item3", toDoList.get(2));
	}

	@Test
	void testProcessListCommand() {
		ToDoCliApp.main(new String[]{"list"});
		assertEquals("1. item1\n" + "2. item2\n", outContent.toString());
	}

	@Test
	void testProcessAddCommand() {
		ToDoCliApp.main(new String[]{"add"});
		assertEquals("Please specify the item to be added to the list like this: add <todo list item>\n", errContent.toString());

		ToDoCliApp.main(new String[]{"add", "item3"});
		ToDoCliApp.main(new String[]{"list"});
		assertEquals("1. item1\n" + "2. item2\n" + "3. item3\n", outContent.toString());
	}

	@Test
	void testProcessDeleteCommand() {
		ToDoCliApp.main(new String[]{"delete"});
		assertEquals("Please specify the item to be deleted from the list like this: delete <number of item in the list>\n", errContent.toString());

		errContent.reset();
		ToDoCliApp.main(new String[]{"delete", "string"});
		assertEquals("Please specify the item to be deleted from the list like this: delete <number of item in the list>\n", errContent.toString());

		errContent.reset();
		ToDoCliApp.main(new String[]{"delete", "4"});
		assertEquals("Please specify item number present in the list to delete.\n", errContent.toString());


		ToDoCliApp.main(new String[]{"delete", "2"});
		ToDoCliApp.main(new String[]{"list"});
		assertEquals("1. item1\n", outContent.toString());
	}

	@Test
	void testProcessCompleteCommand() {
		ToDoCliApp.main(new String[]{"complete"});
		assertEquals("Please specify the item to be completed from the list like this: complete <number of item in the list>\n", errContent.toString());

		errContent.reset();
		ToDoCliApp.main(new String[]{"complete", "string"});
		assertEquals("Please specify the item to be completed from the list like this: complete <number of item in the list>\n", errContent.toString());

		errContent.reset();
		ToDoCliApp.main(new String[]{"complete", "4"});
		assertEquals("Please specify item number present in the list to complete.\n", errContent.toString());


		ToDoCliApp.main(new String[]{"complete", "2"});
		ToDoCliApp.main(new String[]{"list"});
		assertEquals("1. item1\n" + "2. item2 (complete)\n" , outContent.toString());
	}

	@Test
	void testInvalidCommand() {
		ToDoCliApp.main(new String[]{"invalid"});
		assertEquals("Please specify a valid command.\n", errContent.toString());
	}
}