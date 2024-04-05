package com.bengkel.booking.services;

import java.security.Provider.Service;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import com.bengkel.booking.models.BookingOrder;
import com.bengkel.booking.models.Customer;
import com.bengkel.booking.models.ItemService;
import com.bengkel.booking.models.MemberCustomer;
import com.bengkel.booking.models.Vehicle;
import com.bengkel.booking.repositories.ItemServiceRepository;

public class BengkelService {
	static DecimalFormat currencyFormatter = new DecimalFormat("Rp#,##0");
	static List<ItemService> selectedServices = new ArrayList<>();
	static MemberCustomer memberCustomer;

	private static double saldoKoin = 0;
	private static boolean isMember = false;
	private static int loginCount = 0;

	// Silahkan tambahkan fitur-fitur utama aplikasi disini

	// Login
	public static String loginService(String customerID, String customerPassword, List<Customer> listAllCustomers) {
		if (loginCount >= 3) {
			System.out.println("Alert: Maaf, Anda terlalu banyak percobaan");
			System.exit(0);
		}

		Optional<Customer> checkCustomer = listAllCustomers.stream()
				.filter(customer -> customer.getCustomerId().equals(customerID))
				.findFirst();
		if (checkCustomer.isPresent()) {
			Customer customer = checkCustomer.get();
			if (customer.getPassword().equals(customerPassword)) {
				System.out.println("Login Berhasil");
				return customer.getCustomerId();
			} else {
				System.out.println("Alert: Maaf Password Yang Anda Masukkan Salah!");
				loginCount++;
				return null;
			}
		} else {
			System.out.println("Alert: Maaf, Customer Tidak Ditemukan atau Salah!");
			loginCount++;
			return null;
		}
	}

	// Info Customer
	public static Boolean getCustomerInformationService(String customerID, List<Customer> listAllCustomers) {
		Optional<Customer> customer = listAllCustomers.stream()
				.filter(c -> c.getCustomerId().equals(customerID))
				.findFirst();
		if (customer.isPresent()) {
			if (customer.get() instanceof MemberCustomer) {
				memberCustomer = (MemberCustomer) customer.get();

				isMember = true;
				saldoKoin = memberCustomer.getSaldoCoin();
			}

			System.out.println("Data Informasi Customer:");
			System.out.println("Customer ID: " + customer.get().getCustomerId());
			System.out.println("Nama: " + customer.get().getName());
			System.out.println("Customer Status: " + (isMember ? "Member" : "none"));
			System.out.println("Alamat: " + customer.get().getAddress());
			System.out.println("Saldo Koin: " + currencyFormatter.format(saldoKoin));
			System.out.println("List Kendaraan: ");
			customer.get().getVehicles().stream()
					.map(Vehicle::getBrand)
					.forEach(brand -> System.out.println(" - " + brand));
		} else {
			System.out.println("Gagal menampilkan informasi");
		}
		return true;
	}

	// Booking atau Reservation
	public static void bookingService(String customerID, List<Customer> listAllCustomers,
			List<ItemService> listAllServices, Scanner input, List<BookingOrder> bookingOrders) {
		Optional<Customer> customer = listAllCustomers.stream()
				.filter(c -> c.getCustomerId().equals(customerID))
				.findFirst();
		if (customer.isPresent()) {
			if (customer.get() instanceof MemberCustomer) {
				MemberCustomer memberCustomer = (MemberCustomer) customer.get();

				isMember = true;
				saldoKoin = memberCustomer.getSaldoCoin();
			}

			System.out.println("Silahkan Masukkan Vehicle ID: ");
			String vehicleInputID = input.nextLine();

			Optional<Vehicle> vehicle = customer.get().getVehicles().stream()
					.filter(vehicleId -> vehicleId.getVehiclesId().equals(vehicleInputID))
					.findFirst();
			if (vehicle.isPresent()) {
				// Select Services
				boolean addMoreServices = true;
				int selectedServicesCount = 0;
				while (addMoreServices) {
					PrintService.printServicesData(listAllServices, vehicle);
					System.out.println("Silahkan pilih service ID:");
					String serviceId = input.nextLine();
					Optional<ItemService> service = PrintService.getServiceByServiceId(serviceId, listAllServices);
					if (service.isPresent()) {
						selectedServices.add(service.get());
						selectedServicesCount++;
						System.out.println("Service '" + service.get().getServiceName() + "' ditambahkan.");
						if (isMember && selectedServicesCount < 2) {
							System.out.println("Ingin pilih service yang lain? (Y/T)?");
							String choice = input.nextLine();
							if (!choice.equalsIgnoreCase("Y")) {
								addMoreServices = false;
							}
						} else {
							addMoreServices = false;
						}
					} else {
						System.out.println("Service ID " + serviceId + " tidak ditemukan.");
					}
				}

				// Select Payment Method
				double totalHarga = 0;
				double totalDiscount = 0;
				int dicountPercent = 10;
				int paymentSelected = 0;
				if (!selectedServices.isEmpty()) {
					for (ItemService service : selectedServices) {
						totalHarga += service.getPrice();
						totalDiscount = (totalHarga * dicountPercent) / 100;
					}

					if (isMember) {
						System.out.println("Metode Pembayaran:\n" + "1. Saldo Coin\n" + "2. Cash");
						paymentSelected = input.nextInt();
						if (paymentSelected != 1 || paymentSelected != 2) {
							System.out.println("Metode pembayaran tidak diketahui");
						}
					}

					System.out.println();
					System.out.println("Booking berhasil!!!");
					System.out.print("Total Harga Service: ");
					System.out.println(currencyFormatter.format(totalHarga));
					System.out.print("Total Pembayaran: ");
					System.out.println(
							(isMember) ? paymentSelected == 1 ? currencyFormatter.format(totalHarga - totalDiscount)
									: currencyFormatter.format(totalHarga) : currencyFormatter.format(totalHarga));

					// Save Data
					BookingOrder booking = BookingOrder.builder()
							.bookingId("Book-" + customerID + "-" + bookingOrders.size() + 1)
							.customer(customer.get())
							.paymentMethod(isMember ? paymentSelected == 1 ? "Saldo Coin" : "Cash" : "Cash")
							.totalServicePrice(totalHarga)
							.totalPayment((isMember) ? paymentSelected == 1 ? totalHarga - totalDiscount
									: totalHarga : totalHarga)
							.services(selectedServices)
							.build();
					bookingOrders.add(booking);
				}

				// Update Saldo
				if (customer.get() instanceof MemberCustomer) {
					memberCustomer = (MemberCustomer) customer.get();

					MemberCustomer.builder()
							.saldoCoin(988)
							.build();

				}
			} else {
				System.out.println("Kendaraan tidak ditemukan");
			}
		}
	}

	// Top Up Saldo Coin Untuk Member Customer
	public static void topupSaldoService(String customerID, List<Customer> listAllCustomers, Scanner input) {
		Optional<Customer> customer = listAllCustomers.stream()
				.filter(c -> c.getCustomerId().equals(customerID))
				.findFirst();
		if (customer.isPresent()) {
			if (customer.get() instanceof MemberCustomer) {
				System.out.print("Jumlah topup saldo: ");
				int jumlahTopup = input.nextInt();

				MemberCustomer memberCustomer = (MemberCustomer) customer.get();

				if (memberCustomer.getCustomerId() == customerID) {
					double updatedSaldo = memberCustomer.topupSaldo(jumlahTopup);

					memberCustomer.setSaldoCoin(updatedSaldo);
					System.out.println("Saldo berhasil topup.");
					System.out.println("Jumlah saldo saat ini: " + currencyFormatter.format(updatedSaldo));
				} else {
					System.out.println("Saldo gagal topup.");
				}
			} else {
				System.out.println("Maaf fitur ini hanya untuk Member saja!");
			}
		}
	}

	// Logout
}
