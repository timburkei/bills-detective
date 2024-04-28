import {Article} from "./article";

export class Receipt {
  receiptId: number;
  date: string;
  chain: string;
  chainAddress: string;
  articles: Article[];

  constructor(
    receiptId: number,
    date: string,
    chain: string,
    chainAddress: string,
    articles: Article[]
  ) {
    this.receiptId = receiptId;
    this.date = date;
    this.chain = chain;
    this.chainAddress = chainAddress;
    this.articles = articles;
  }
}
