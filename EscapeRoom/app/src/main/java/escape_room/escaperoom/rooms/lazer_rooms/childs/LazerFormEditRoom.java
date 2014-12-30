package escape_room.escaperoom.rooms.lazer_rooms.childs;

import android.view.View;

import escape_room.escaperoom.R;
import escape_room.escaperoom.rooms.lazer_rooms.parent.LazerParentRoom;

/**
 * Created by Dell on 21.12.2014.
 */
public class LazerFormEditRoom extends LazerParentRoom{

    @Override
    protected void specificAction() {
        thisRoom.findViewById(R.id.formApply).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dataService.getFormSet().get(dataService.getModification().getRoomNumber())
                        .getLazerBitSet().setBitSet(lazerBitSet);

                getFragmentManager().popBackStack();
            }
        });
    }

    @Override
    protected void additionalRecovery() {

        lazerBitSet.setBitSet(dataService.getFormSet().get(dataService.getModification()
                .getRoomNumber()).getLazerBitSet());

    }

    @Override
    protected void lazerButtonAdditionalAction() {}
}
