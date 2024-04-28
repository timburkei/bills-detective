export class Article {
  name: string;
  price: number;
  subCategory: string;
  category: string;

  constructor(
    name: string,
    price: number,
    subCategory: string,
    category: string
  ) {
    this.name = name;
    this.price = price;
    this.subCategory = subCategory;
    this.category = category;
  }
}
