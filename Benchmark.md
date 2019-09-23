# General benchmark settings

All benchmarks are using:

- **Distinct partitions key count**: 100000000

- **Distinct rows per partition**: 1000000

- **Consistency level**: Session

- **Duration in minutes**: 10

- **Ubuntu 16.04LTS VM with accelerated networking**

- **Azure.CosmosDB service endpoint enabled on the subnet of the injector(s)**

- **West Europe region**

 
## Write operations

### Global results

| Target Throughput | Real Throughput | Provisioned RU/s | Concurrency level | Injector Count | VM Size                       |
|-------------------|-----------------|------------------|-------------------|----------------|-------------------------------|
| 5k RPS            | 5 631.65 RPS    |  32 000          | 27                | 1              | D16s_v3 (16 vCPUs, 64Gb RAM)  |
| 10k RPS           | 10 487.74 RPS   |  65 000          | 55                | 1              | D16s_v3 (16 vCPUs, 64Gb RAM)  |
| 20k RPS           | 20 011.46 RPS   | 130 000          | 140               | 1              | D32s_v3 (32 vCPUs, 128Gb RAM) |
| 50k RPS           | 49 987.84 RPS   | 330 000          | 260               | 1              | D64s_v3 (64 vCPUs, 256Gb RAM) |
| 100k RPS          | 98 637.22 RPS   | 630 000          | 540               | 2              | D32s_v3 (32 vCPUs, 128Gb RAM) |
| 150k RPS          | 146 382.97      | 850 000          | 812               | 4              | D32s_v3 (32 vCPUs, 128Gb RAM) |

Projection of Throughput for 300k RPS = 630 000 * 3 = 1 800 000 RUs/s

### Detailed latency in millisecs

| Target Throughput | Real Throughput | Min  | 50%  | 75%  | 90%  | 95%  | 98%  | 99%   | 99.9% | 99.99% | 99.999% | Max     |
|-------------------|-----------------|------|------|------|------|------|------|-------|-------|--------|---------|---------|
| 5k RPS            | 5 631.65 RPS    | 2.00 | 6.06 | 6.39 | 6.72 | 6.98 | 7.63 | 8.65  | 15.01 | 45.88  | 411.04  | 528.48  |
| 10k RPS           | 10 487.74 RPS   | 3.41 | 5.03 | 5.37 | 5.73 | 6.13 | 7.01 | 8.72  | 15.53 | 55.31  | 314.57  | 379.58  |
| 20k RPS           | 20 011.46 RPS   | 4.23 | 6.23 | 6.72 | 7.34 | 8.00 | 9.63 | 12.19 | 23.86 | 67.11  | 566.23  | 1019.22 |
| 50k RPS           | 49 987.84 RPS   | 3.28 | 5.01 | 5.34 | 5.70 | 6.13 | 7.57 | 10.35 | 21.23 | 57.67  | 199.23  | 1140.85 |
| 100k RPS          | 98 637.22 RPS   | 3.21 | 5.05 | 5.37 | 5.80 | 6.29 | 7.63 | 9.50  | 20.05 | 100.66 | 3271.56 | 6912.21 |
| 150k RPS          | 146 382.97      | 3.38 | 5.34 | 5.83 | 6.55 | 7.27 | 9.11 | 11.27 | 40.63 | 126.35 | 392.17  | 734.00  |

### Pricing

#### Throughput cost

* 8h/day scenario for West Europe region
* 31 days/month
* 12 months/year

| Target Throughput |Provisioned RUs/s | Daily cost | Monthly cost | Annual cost  |
|-------------------|------------------|------------|--------------|--------------|
| 5k RPS            |  32 000          |  17.20€    |    533.20€   |    6 398.40€ |
| 10k RPS           |  65 000          |  35.08€    |  1 087.48€   |   13 049.76€ |
| 20k RPS           | 130 000          |  70.16€    |  2 174.96€   |   26 099.52€ |
| 50k RPS           | 330 000          | 178.10€    |  5 512.10€   |   66 145.20€ |
| 100k RPS          | 630 000          | 340.02€    | 10 540.62€   |  126 487.44€ |
| 150k RPS          | 850 000          | 458.76€    | 14 221.56€   |  170 658.72€ |

Projection of cost for 300k RPS write workload 
-   daily cost:     971.48€
- monthly cost:  30 115.88€
-  annual cost: 361 390.56€ 

Projection of cost for 300k RPS write workload 
-   daily cost                     :     971.48€
- monthly cost (5 days/7 = 20 days):  19 429.60€
-  annual cost                     : 233 155.20€      
                    
#### Storage cost

* 8h/day scenario for West Europe region
* 31 days/month
* 12 months/year
* 1 kb/insert

| Target Throughput |Daily storage size | Monthly cost | Annual cost |
|-------------------|-------------------|--------------|-------------|
| 5k RPS            |  137Gb            |    28.88€    |     346.56€ |
| 10k RPS           |  274Gb            |    57.77€    |     693.24€ |
| 20k RPS           |  549Gb            |   115.74€    |   1 388.88€ |
| 50k RPS           |  1.3Tb            |   274.07€    |   3 288.84€ |
| 100k RPS          |  2.7Tb            |   569.23€    |   6 830.76€ |
| 150k RPS          |  4.1Tb            |   864.38€    |  10 372.56€ |

Projection of Storage for 300k RPS 
- monthly cost = 8.1Tb = 1707.68€
- annual cost = 97.2Tb = 20 492.19€

  