package com.bengkel.booking.services;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

import com.bengkel.booking.models.Customer;
import com.bengkel.booking.models.ItemService;
import com.bengkel.booking.repositories.CustomerRepository;
import com.bengkel.booking.repositories.ItemServiceRepository;

public class MenuService {
	private static List<Customer> listAllCustomers = CustomerRepository.getAllCustomer();
	private static List<ItemService> listAllItemService = ItemServiceRepository.getAllItemService();
	private static Scanner input = new Scanner(System.in);

	private static String customerID = null;

	public static void run() {
		boolean isLooping = true;
		do {
			login();
			// mainMenu();
		} while (isLooping);

	}

	public static void login() {
		System.out.print("Masukkan Customer ID: ");
		String inputCustomerID = input.nextLine();
		System.out.print("Masukkan Password: ");
		String inputCustomerPassword = input.nextLine();

		String bengkelService = BengkelService.loginService(inputCustomerID, inputCustomerPassword, listAllCustomers);
		if (bengkelService != null) {
			customerID = bengkelService;
			mainMenu();
		}
	}

	public static void mainMenu() {
		String[] listMenu = { "Informasi Customer", "Booking Bengkel", "Top Up Bengkel Coin", "Informasi Booking",
				"Logout" };
		int menuChoice = 0;
		boolean isLooping = true;

		do {
			PrintService.printMenu(listMenu, "Booking Bengkel Menu");
			menuChoice = Validation.validasiNumberWithRange("Masukan Pilihan Menu:", "Input Harus Berupa Angka!",
					"^[0-9]+$", listMenu.length - 1, 0);
			System.out.println(menuChoice);

			switch (menuChoice) {
				case 1:
					// panggil fitur Informasi Customer
					BengkelService.getCustomerInformationService(customerID, listAllCustomers);
					break;
				case 2:
					// panggil fitur Booking Bengkel
					break;
				case 3:
					// panggil fitur Top Up Saldo Coin
					BengkelService.topupSaldoService(customerID, listAllCustomers, input);
					break;
				case 4:
					// panggil fitur Informasi Booking Order
					break;
				default:
					// panggil fitur Logout
					if (customerID != null) {
						System.out.println("Logout berhasil");
						isLooping = false;
						customerID = null;
					} else {
						System.out.println("Logout gagal");
					}
					break;
			}
		} while (isLooping);

	}

	// Silahkan tambahkan kodingan untuk keperluan Menu Aplikasi
}
