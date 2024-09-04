package vti.travel.Model.Request;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchBookingRq extends BaseRequest {

    private String fullName;
    private String address;

}
