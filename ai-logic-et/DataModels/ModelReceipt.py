from dataclasses import dataclass, field
from typing import List

'''
This class defines the structure, types and default values of the invoices.
It is used for standadizing the response from the FastAPI application to the Java Springtboot Backend.
'''
@dataclass
class Item:
    name: str = "Der Gerät"
    price: float = 0.0


@dataclass
class StoreInfo:
    invoiceFileId: int = 0
    streetStore: str = "Kieler Str."
    numberStore: str = '545a'
    zipStore: int = 22525
    cityStore: str = "Hamburg"
    nameStore: str = "Feinkost Kolinski"
    invoiceDate: str = "01.01.2024"
    invoiceTime: str = "01:00:00"
    totalAmount: float = 0.0
    taxes7Amount: float = 0.0
    taxes19Amount: float = 0.0
    discountsAmount: float = 0.0
    userId: str = "auth0|65630d5317b4bdb501144ab5"
    invoiceItems: List[Item] = field(default_factory=list)

    def calculate_total_amount(self):
        try:
            total_price = 0.0
            for item in self.invoiceItems:
                if isinstance(item.price, float):
                    total_price += item.price
            total_price = round(total_price, 2)
            return total_price
        except:
            return 0.0
