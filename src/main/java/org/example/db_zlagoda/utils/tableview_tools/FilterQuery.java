package org.example.db_zlagoda.utils.tableview_tools;

import org.example.db_zlagoda.utils.SaleFilter;

public record FilterQuery(String searchBy, String sortBy, String tableType, String query, CategoryItem categoryFilter,
                          SaleFilter saleFilter) {
}
