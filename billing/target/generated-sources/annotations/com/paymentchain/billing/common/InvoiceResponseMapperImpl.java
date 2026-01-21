package com.paymentchain.billing.common;

import com.paymentchain.billing.dto.InvoiceResponse;
import com.paymentchain.billing.entities.Invoice;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-01-21T17:35:53-0500",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.45.0.v20260101-2150, environment: Java 25.0.1 (Oracle Corporation)"
)
@Component
public class InvoiceResponseMapperImpl implements InvoiceResponseMapper {

    @Override
    public InvoiceResponse InvoiceToInvoiceRespose(Invoice source) {
        if ( source == null ) {
            return null;
        }

        InvoiceResponse invoiceResponse = new InvoiceResponse();

        invoiceResponse.setCustomer( source.getCustomerId() );
        invoiceResponse.setInvoiceId( source.getId() );
        invoiceResponse.setAmount( source.getAmount() );
        invoiceResponse.setDetail( source.getDetail() );
        invoiceResponse.setNumber( source.getNumber() );

        return invoiceResponse;
    }

    @Override
    public List<InvoiceResponse> InvoiceListToInvoiceResposeList(List<Invoice> source) {
        if ( source == null ) {
            return null;
        }

        List<InvoiceResponse> list = new ArrayList<InvoiceResponse>( source.size() );
        for ( Invoice invoice : source ) {
            list.add( InvoiceToInvoiceRespose( invoice ) );
        }

        return list;
    }

    @Override
    public Invoice InvoiceResponseToInvoice(InvoiceResponse srr) {
        if ( srr == null ) {
            return null;
        }

        Invoice invoice = new Invoice();

        invoice.setCustomerId( srr.getCustomer() );
        invoice.setId( srr.getInvoiceId() );
        invoice.setAmount( srr.getAmount() );
        invoice.setDetail( srr.getDetail() );
        invoice.setNumber( srr.getNumber() );

        return invoice;
    }

    @Override
    public List<Invoice> InvoiceResponseToInvoiceList(List<InvoiceResponse> source) {
        if ( source == null ) {
            return null;
        }

        List<Invoice> list = new ArrayList<Invoice>( source.size() );
        for ( InvoiceResponse invoiceResponse : source ) {
            list.add( InvoiceResponseToInvoice( invoiceResponse ) );
        }

        return list;
    }
}
