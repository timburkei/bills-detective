import {Component, Input, OnInit} from '@angular/core';
import { CommonModule } from '@angular/common';
import {NgApexchartsModule} from "ng-apexcharts";

@Component({
  selector: 'app-line-chart',
  standalone: true,
  imports: [CommonModule, NgApexchartsModule],
  template: `
    <div id="chart">
      <apx-chart
        [series]="chartData.series"
        [chart]="chartData.chart"
        [xaxis]="chartData.xaxis"
        [stroke]="chartData.stroke"
      ></apx-chart>
    </div>
  `,
  styleUrl: './line-chart.component.css'
})
export class LineChartComponent implements OnInit {
  @Input() chartData: any;

  ngOnInit() {
    /*this.chartData = {
      series: [
        {
          name: 'Series 1',
          data: [30, 40, 45, 50, 49, 60],
        },
      ],
      xaxis: {
        categories: ['Januar', 'Februar', 'März', 'April',
          'Mai', 'Juni'
        ],
      },
      stroke: {
        curve: 'smooth',
      },
      chart: {
        type: 'line',
        height: 350,
      },
    };*/
  }
}
