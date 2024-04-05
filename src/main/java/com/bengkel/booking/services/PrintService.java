package com.bengkel.booking.services;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.text.DecimalFormat;
import java.util.stream.Collectors;
import java.util.ArrayList;

import com.bengkel.booking.models.BookingOrder;
import com.bengkel.booking.models.Car;
import com.bengkel.booking.models.ItemService;
import com.bengkel.booking.models.Vehicle;

public class PrintService {
	static DecimalFormat currencyFormatter = new DecimalFormat("Rp#,##0");
	static List<BookingOrder> bookingOrders = new ArrayList<>();

	public static void printMenu(String[] listMenu, String title) {
		String line = "+---------------------------------+";
		int number = 1;
		String formatTable = " %-2s. %-25s %n";

		System.out.printf("%-25s %n", title);
		System.out.println(line);

		for (String data : listMenu) {
			if (number < listMenu.length) {
				System.out.printf(formatTable, number, data);
			} else {
				System.out.printf(formatTable, 0, data);
			}
			number++;
		}
		System.out.println(line);
		System.out.println();
	}

	public static void printVechicle(List<Vehicle> listVehicle) {
		String formatTable = "| %-2s | %-15s | %-10s | %-15s | %-15s | %-5s | %-15s |%n";
		String line = "+----+-----------------+------------+-----------------+-----------------+-------+-----------------+%n";
		System.out.format(line);
		System.out.format(formatTable, "No", "Vechicle Id", "Warna", "Brand", "Transmisi", "Tahun", "Tipe Kendaraan");
		System.out.format(line);
		int number = 1;
		String vehicleType = "";
		for (Vehicle vehicle : listVehicle) {
			if (vehicle instanceof Car) {
				vehicleType = "Mobil";
			} else {
				vehicleType = "Motor";
			}
			System.out.format(formatTable, number, vehicle.getVehiclesId(), vehicle.getColor(), vehicle.getBrand(),
					vehicle.getTransmisionType(), vehicle.getYearRelease(), vehicleType);
			number++;
		}
		System.out.printf(line);
	}

	// Silahkan Tambahkan function print sesuai dengan kebutuhan.

	public static void printServicesData(List<ItemService> listAllServices, Optional<Vehicle> vehicle) {
		AtomicInteger count = new AtomicInteger(0);

		System.out.println("List Service yang tersedia:");
		System.out.println(
				"---------------------------------------------------------------------------");
		System.out.printf("| %-5s | %-10s | %-20s | %-10s | %-10s |\n", "No", "Service ID",
				"Nama Service", "Tipe Kendaraan", "Harga");
		System.out.println(
				"---------------------------------------------------------------------------");
		listAllServices.stream()
				.filter(vehicleType -> vehicleType.getVehicleType().equals(vehicle.get().getVehicleType()))
				.forEach(service -> {
					System.out.printf("| %-5s | %-10s | %-20s | %-14s | %-10s |\n", count.incrementAndGet(),
							service.getServiceId(), service.getServiceName(),
							service.getVehicleType(),
							currencyFormatter.format(service.getPrice()));
				});
		System.out.println(
				"---------------------------------------------------------------------------");
	}

	public static Optional<ItemService> getServiceByServiceId(String serviceId, List<ItemService> listAllServices) {
		Optional<ItemService> services = listAllServices.stream()
				.filter(vehicleId -> vehicleId.getServiceId().equals(serviceId))
				.findFirst();
		if (services.isPresent()) {
			return services;
		} else {
			return null;
		}
	}

	public static void getAllBookingInformations(List<BookingOrder> bookingOrders) {
		AtomicInteger count = new AtomicInteger(0);

		System.out.println("Booking Order:");
		System.out.println(
				"-----------------------------------------------------------------------------------------------------------------------------------------");
		System.out.printf("| %-5s | %-20s | %-20s | %-20s | %-15s | %-15s | %-20s |\n", "No", "Booking ID",
				"Nama Customer", "Payment Method", "Total Service", "Total Payment", "List Service");
		System.out.println(
				"-----------------------------------------------------------------------------------------------------------------------------------------");
		bookingOrders.stream()
				.forEach(booking -> {
					System.out.printf("| %-5s | %-20s | %-20s | %-20s | %-15s | %-15s | %-20s |\n",
							count.incrementAndGet(),
							booking.getBookingId(),
							booking.getCustomer().getName(),
							booking.getPaymentMethod(),
							booking.getTotalServicePrice(),
							booking.getTotalPayment(),
							booking.getServices().stream().map(ItemService::getServiceName)
									.collect(Collectors.toList()));
				});

		System.out.println(
				"-----------------------------------------------------------------------------------------------------------------------------------------");
	}
}
